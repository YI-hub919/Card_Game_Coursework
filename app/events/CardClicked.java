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

		if (!gameState.isPlayer1Turn) {
			if (out != null) BasicCommands.addPlayer1Notification(out, "Not your turn", 2);
			return;
		}

		int rawPos = posNode.asInt();
		var hand = gameState.player1.getCardInHand();
		int handSize = hand.size();

		int idx;

		// Accept BOTH styles:
		// - 0-based: 0..handSize-1
		// - 1-based: 1..handSize
		if (rawPos >= 0 && rawPos < handSize) {
			idx = rawPos;          // 0-based
		} else if (rawPos >= 1 && rawPos <= handSize) {
			idx = rawPos - 1;      // 1-based
		} else {
			gameState.selectedHandCard = -1;
			if (out != null) BasicCommands.addPlayer1Notification(out, "No card here", 2);
			return;
		}

		gameState.selectedHandCard = idx;

		if (out != null) {
			BasicCommands.addPlayer1Notification(out, "Selected card slot: " + rawPos, 2);
		}
	}

}
