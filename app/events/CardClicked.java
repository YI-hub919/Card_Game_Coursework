package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

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

		int handPosition = posNode.asInt() - 1;

		// Only allow selection on player1's turn
		if (!gameState.isPlayer1Turn) {
			if (out != null) BasicCommands.addPlayer1Notification(out, "Not your turn", 2);
			return;
		}

		gameState.selectedHandCard = handPosition;

		if (out != null) {
			BasicCommands.addPlayer1Notification(out, "Selected card: " + handPosition, 2);
		}

		if (handPosition < 0) return;


	}

}
