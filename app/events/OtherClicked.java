package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
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
		// "Cancel" only applies when something is selected; clicking on a blank area afterwards will not display anything
		boolean hadSelection = gameState.selectedTile != null
				|| gameState.selectedUnit != null
				|| (gameState.validMoveTiles != null && !gameState.validMoveTiles.isEmpty())
				|| gameState.selectedHandCard != -1;

		gameState.selectedHandCard = -1;

		if (out != null) {
			if (gameState.selectedTile != null) {
				BasicCommands.drawTile(out, gameState.selectedTile, 0);
			}
			// Clear the highlight of the movable grid of the previously selected unit.
			if (gameState.validMoveTiles != null) {
				for (Tile t : gameState.validMoveTiles) {
					BasicCommands.drawTile(out, t, 0);
				}
				gameState.validMoveTiles.clear();
			}
		}
		gameState.selectedTile = null;
		gameState.selectedUnit = null;

		// If it has already been cancelled, it will no longer be displayed.
		if (out != null && hadSelection) {
			BasicCommands.addPlayer1Notification(out, "Cancelled", 1);
		}
	}

}


