import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.TileClicked;
import org.junit.Test;
import play.libs.Json;
import structures.Board;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Card;

import static org.junit.Assert.*;
import utils.StaticConfFiles;

public class SummoningTest {

    private ObjectNode tileMsg(int x, int y) {
        ObjectNode n = Json.newObject();
        n.put("messagetype", "tileclicked");
        n.put("tilex", x);
        n.put("tiley", y);
        return n;
    }

    @Test
    public void testSummonConsumesManaRemovesHandAndAddsUnit() {
        // Make BasicCommands safe under unit tests (out can be null)
        BasicCommands.altTell = new CheckMessageIsNotNullOnTell();

        GameState gs = new GameState();
        gs.board = new Board();
        gs.isPlayer1Turn = true;

        // Prepare a creature card in hand
        Card c = new Card();
        c.setCardname("Test Wraithling");
        c.setManacost(1);
        c.setCreature(true);
        c.setUnitConfig(StaticConfFiles.wraithling); // exists in template
        c.setBigCard(new BigCard(1, 1, new String[]{}, new String[]{}));

        gs.player1.setMana(2);
        gs.player1.getCardInHand().clear();
        gs.player1.getCardInHand().add(c);

        // Select the first card (index 0) like CardClicked would do
        gs.selectedHandCard = 0;

        // Summon onto (3,3)
        new TileClicked().processEvent(null, gs, tileMsg(3, 3));

        // Mana spent
        assertEquals(1, gs.player1.getMana());

        // Card removed from hand
        assertEquals(0, gs.player1.getCardInHand().size());

        // Unit added
        assertEquals(1, gs.player1SummonedUnits.size());

        var u = gs.player1SummonedUnits.get(0);
        assertNotNull(u);
        assertNotNull(u.getPosition());
        assertEquals(3, u.getPosition().getTilex());
        assertEquals(3, u.getPosition().getTiley());

        // Unit stats come from card big card
        assertEquals(1, u.getAttack());
        assertEquals(1, u.getHealth());
    }

    @Test
    public void testCannotSummonOnOccupiedTile() {
        BasicCommands.altTell = new CheckMessageIsNotNullOnTell();

        GameState gs = new GameState();
        gs.board = new Board();
        gs.isPlayer1Turn = true;

        // Prepare first creature card
        Card first = new Card();
        first.setCardname("Unit A");
        first.setManacost(1);
        first.setCreature(true);
        first.setUnitConfig(StaticConfFiles.wraithling);
        first.setBigCard(new BigCard(1, 1, new String[]{}, new String[]{}));

        gs.player1.setMana(2);
        gs.player1.getCardInHand().clear();
        gs.player1.getCardInHand().add(first);
        gs.selectedHandCard = 0;

        // First summon at (3,3)
        new TileClicked().processEvent(null, gs, tileMsg(3, 3));

        assertEquals(1, gs.player1SummonedUnits.size());

        // Prepare second creature card
        Card second = new Card();
        second.setCardname("Unit B");
        second.setManacost(1);
        second.setCreature(true);
        second.setUnitConfig(StaticConfFiles.wraithling);
        second.setBigCard(new BigCard(1, 1, new String[]{}, new String[]{}));

        gs.player1.getCardInHand().add(second);
        gs.player1.setMana(2);
        gs.selectedHandCard = 0;

        // Try summoning on the same tile
        new TileClicked().processEvent(null, gs, tileMsg(3, 3));

        // Should not create a new unit
        assertEquals(1, gs.player1SummonedUnits.size());

        // Card should remain in hand
        assertEquals(1, gs.player1.getCardInHand().size());

        // Mana should not be reduced
        assertEquals(2, gs.player1.getMana());
    }
}