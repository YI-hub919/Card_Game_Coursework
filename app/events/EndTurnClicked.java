package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import commands.BasicCommands;
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
		// Toggle turn
		gameState.isHumanTurn = !gameState.isHumanTurn;

		// Prepare text
		String turnText;

		if (gameState.isHumanTurn) {
			turnText = "Your turn";
		} else {
			turnText = "Opponent's turn";
		}

		// Show notification
		BasicCommands.addPlayer1Notification(out, turnText, 2);
	}

}
