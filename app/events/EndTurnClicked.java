package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// Mark that end turn has been requested
		gameState.endTurnRequested = true;

		// Set phase to pending
		gameState.phase = GameState.TurnPhase.END_TURN_PENDING;

		if (gameState.player1Avatar != null) {
			gameState.player1Avatar.setHasAttacked(false);
			gameState.player1Avatar.setHasMoved(false);
		}
		if (gameState.player2Avatar != null) {
			gameState.player2Avatar.setHasAttacked(false);
			gameState.player2Avatar.setHasMoved(false);
		}
        
		for (Unit unit: gameState.player1SummonedUnits) {
			unit.setHasAttacked(false);
			unit.setHasMoved(false);
		}
		for (Unit unit: gameState.player2SummonedUnits) {
			unit.setHasAttacked(false);
			unit.setHasMoved(false);
		}
	}

}
