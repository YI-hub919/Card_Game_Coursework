package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		gameState.selectedHandCard = -1;

		if (gameState.selectedTile != null && out != null) {
			BasicCommands.drawTile(out, gameState.selectedTile, 0);
		}
		gameState.selectedTile = null;
		gameState.selectedUnit = null;

		if (out != null) BasicCommands.addPlayer1Notification(out, "Cancelled", 1);
		
	}

}


