package events;


import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		if (message == null) return;
		JsonNode posNode = message.get("position");
		if (posNode == null) return;

		if (!gameState.isPlayer1Turn) {
			if (out != null) BasicCommands.addPlayer1Notification(out, "Not your turn", 2);
			return;
		}

		int rawPos = posNode.asInt();
		int idx = rawPos - 1;

		var hand = gameState.player1.getCardInHand();
		if (rawPos < 1 || rawPos > 6 || idx >= hand.size()) {
			gameState.selectedHandCard = -1;
			if (out != null) BasicCommands.addPlayer1Notification(out, "No card here", 2);
			return;
		}

		if (gameState.selectedHandCard == idx) {
			gameState.selectedHandCard = -1;
			if (out != null) BasicCommands.addPlayer1Notification(out, "Card unselected", 2);
			return;
		}

		// Remove the highlight of some units
		if (out != null) {
			if (gameState.selectedTile != null) {
				BasicCommands.drawTile(out, gameState.selectedTile, 0);
			}
			if (gameState.validMoveTiles != null) {
				for (Tile t : gameState.validMoveTiles) {
					BasicCommands.drawTile(out, t, 0);
				}
				gameState.validMoveTiles.clear();
			}
		}
		gameState.selectedTile = null;
		gameState.selectedUnit = null;

		gameState.selectedHandCard = idx;
		if (out != null) BasicCommands.addPlayer1Notification(out, "Card selected", 2);
	}

}
