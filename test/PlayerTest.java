import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import structures.basic.Player;
import utils.OrderedCardLoader;

public class PlayerTest {

    @Before
    public void setUp() {
        BasicCommands.altTell = new CheckMessageIsNotNullOnTell();
    }

    // Health Tests

    @Test
    public void testInitialHealth() {
        Player player = new Player(20, 0);
        assertEquals(20, player.getHealth());
    }

    @Test
    public void testSetHealth() {
        Player player = new Player(20, 0);
        player.setHealth(10);
        assertEquals(10, player.getHealth());
    }

    @Test
    public void testHealthReducedToZero() {
        Player player = new Player(20, 0);
        player.setHealth(0);
        assertEquals(0, player.getHealth());
    }

    @Test
    public void testHealthCannotGoNegative() {
        Player player = new Player(20, 0);
        player.setHealth(-1);
        assertTrue("Health should not be negative", player.getHealth() >= 0);
    }

    // Meta Tests

    @Test
    public void testInitialMana() {
        Player player = new Player(20, 0);
        assertEquals(0, player.getMana());
    }

    @Test
    public void testSetMana() {
        Player player = new Player(20, 0);
        player.setMana(3);
        assertEquals(3, player.getMana());
    }

    @Test
    public void testManaMaxValue() {
        Player player = new Player(20, 0);
        player.setMana(9);
        assertEquals(9, player.getMana());
    }

    @Test
    public void testManaResetToZero() {
        Player player = new Player(20, 0);
        player.setMana(5);
        player.setMana(0);
        assertEquals(0, player.getMana());
    }

    // Draw Card Tests

    @Test
    public void testDrawCardAddsToHand() {
        Player player = new Player(20, 0);
        player.setCardDeck(OrderedCardLoader.getPlayer1Cards(2));
        Player.drawCard(null, player, true);
        assertEquals(1, player.getCardInHand().size());
    }

    @Test
    public void testDraw3CardsAddsToHand() {
        Player player = new Player(20, 0);
        player.setCardDeck(OrderedCardLoader.getPlayer1Cards(2));
        for (int i = 0; i < 3; i++) {
            Player.drawCard(null, player, true);
        }
        assertEquals(3, player.getCardInHand().size());
    }

    @Test
    public void testDrawCardFromEmptyDeck() {
        Player player = new Player(20, 0);
        player.setCardDeck(new ArrayList<>());
        Player.drawCard(null, player, true);
        assertEquals(0, player.getCardInHand().size());
    }

    @Test
    public void testHandLimitIsSIX() {
        Player player = new Player(20, 0);
        player.setCardDeck(OrderedCardLoader.getPlayer1Cards(2));

        for (int i = 0; i < 8; i++) {
            Player.drawCard(null, player, true);
        }
        assertTrue("Hand size should not exceed 6", player.getCardInHand().size() == 6);
    }
}
