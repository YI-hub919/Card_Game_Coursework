package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import structures.basic.Player;
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
		// Switch turn
		gameState.isHumanTurn = !gameState.isHumanTurn;

		// Prepare text
		String turnText = gameState.isHumanTurn ? "Your turn" : "Opponent's turn";

        // Show notification on correct player'side
		if (gameState.isHumanTurn) {
			BasicCommands.addPlayer1Notification(out, turnText, 2);
		} else {
			BasicCommands.addPlayer2Notification(out, turnText, 2);
		}
		// Mana highlight
		if (gameState.isHumanTurn) {
			gameState.nextRounds();
		}

		int manaCap = gameState.getManaCapacity();
		if (gameState.isHumanTurn) {
			gameState.player1.setMana(manaCap);
		} else {
			gameState.player2.setMana(manaCap);
		}
		BasicCommands.setPlayer1Mana(out, gameState.player1);
		BasicCommands.setPlayer2Mana(out, gameState.player2);
	}

}
