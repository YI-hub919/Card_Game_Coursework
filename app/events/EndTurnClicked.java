package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import com.fasterxml.jackson.databind.JsonNode;
import structures.GameState;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 *
 * {
 *   messageType = "endTurnClicked"
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        // Mark that end turn has been requested and set phase
        gameState.endTurnRequested = true;
        gameState.phase = GameState.TurnPhase.END_TURN_PENDING;

        // Reset all units' attack and movement state
        if (gameState.player1Avatar != null) {
            gameState.player1Avatar.setHasAttacked(false);
            gameState.player1Avatar.setHasMoved(false);
        }
        if (gameState.player2Avatar != null) {
            gameState.player2Avatar.setHasAttacked(false);
            gameState.player2Avatar.setHasMoved(false);
        }
        for (Unit unit : gameState.player1SummonedUnits) {
            unit.setHasAttacked(false);
            unit.setHasMoved(false);
        }
        for (Unit unit : gameState.player2SummonedUnits) {
            unit.setHasAttacked(false);
            unit.setHasMoved(false);
        }

        // Switch turn
        gameState.isHumanTurn = !gameState.isHumanTurn;

        // Show turn notification
        String turnText = gameState.isHumanTurn ? "Your turn" : "Opponent's turn";
        if (gameState.isHumanTurn) {
            BasicCommands.addPlayer1Notification(out, turnText, 2);
        } else {
            BasicCommands.addPlayer2Notification(out, turnText, 2);
        }

        // Advance round counter when it becomes human's turn again
        if (gameState.isHumanTurn) {
            gameState.nextRounds();
        }

        // Update mana for both players
        int manaCap = gameState.getManaCapacity();
        if (gameState.isHumanTurn) {
            gameState.player1.setMana(manaCap);
            BasicCommands.setPlayer1Mana(out, gameState.player1);
            gameState.player2.setMana(0);
            BasicCommands.setPlayer2Mana(out, gameState.player2);
        } else {
            gameState.player2.setMana(manaCap);
            BasicCommands.setPlayer2Mana(out, gameState.player2);
            gameState.player1.setMana(0);
            BasicCommands.setPlayer1Mana(out, gameState.player1);
        }
    }
}
