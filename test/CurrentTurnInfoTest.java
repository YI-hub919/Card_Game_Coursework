import commands.CheckMessageIsNotNullOnTell;
import org.junit.Test;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import events.EndTurnClicked;
import structures.GameState;
import commands.CheckMessageIsNotNullOnTell;

public class CurrentTurnInfoTest {

    @Test
    public void testCurrentTurnInfoAndManaAfterEndTurn() {

        // Make BasicCommands safe in unit tests
        BasicCommands.altTell = new CheckMessageIsNotNullOnTell();

        GameState gameState = new GameState();
        EndTurnClicked handler = new EndTurnClicked();

        // Start as human's turn
        gameState.isPlayer1Turn = true;

        // Ensure players exist
        assertNotNull("player1 should exist", gameState.player1);
        assertNotNull("player2 should exist", gameState.player2);

        // Reset mana to known state
        gameState.player1.setMana(0);
        gameState.player2.setMana(0);

        // Build end turn message
        ObjectNode message = new ObjectMapper().createObjectNode();
        message.put("messageType", "endTurnClicked");

        // Trigger end turn
        handler.processEvent(null, gameState, message);

        // simulate heartbeat to finalize turn
        new events.Heartbeat().processEvent(null, gameState, message);

        // Turn should switch
        assertFalse("Turn should switch to opponent", gameState.isPlayer1Turn);

        // Mana capacity after switching
        int expectedMana = gameState.getManaCapacity();

        // Opponent should now have full mana
        assertEquals("Opponent mana should equal capacity",
                expectedMana,
                gameState.player2.getMana());

        // Previous player should be reset to 0
        assertEquals("Previous player mana should be 0",
                0,
                gameState.player1.getMana());
    }
}