import org.junit.Test;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import events.EndTurnClicked;
import structures.GameState;

public class CurrentTurnInfoTest {

    @Test
    public void testCurrentTurnInfoAndManaAfterEndTurn() {
        // Make BasicCommands safe in unit tests

        GameState gameState = new GameState();
        EndTurnClicked handler = new EndTurnClicked();

        // Current turn info: start as human's turn
        gameState.isHumanTurn = true;

        // If your GameState already initialises these in dev, these should exist
        assertNotNull("player1 should exist in GameState", gameState.player1);
        assertNotNull("player2 should exist in GameState", gameState.player2);

        // Set both mana to known values first (so we can verify update)
        gameState.player1.setMana(0);
        gameState.player2.setMana(0);

        // Build message for EndTurnClicked
        ObjectNode message = new ObjectMapper().createObjectNode();
        message.put("messageType", "endTurnClicked");

        // Trigger end turn: should switch to opponent
        handler.processEvent(null, gameState, message);

        // Now current turn belongs to opponent
        assertFalse("After EndTurnClicked, it should be opponent's turn", gameState.isHumanTurn);

        // Mana "highlight" in backend meaning: current player's mana is updated to current capacity
        int expectedMana = gameState.getManaCapacity();
        assertEquals("Opponent mana should update to mana capacity", expectedMana, gameState.player2.getMana());
    }
}