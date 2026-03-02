package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

import commands.BasicCommands;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (gameState.endTurnRequested && gameState.movingUnitsCount == 0) {
			finalizeTurn(out, gameState);
		}
	}

	private void finalizeTurn(ActorRef out, GameState gameState) {

		// consume request
		gameState.endTurnRequested = false;

		// switch turn (sync both flags)
		gameState.isPlayer1Turn = !gameState.isPlayer1Turn;

		// rounds only increase when human starts a new turn
		if (gameState.isPlayer1Turn) {
			gameState.nextRounds();
		}

		// update phase
		gameState.phase = gameState.isPlayer1Turn
				? GameState.TurnPhase.HUMAN_TURN
				: GameState.TurnPhase.AI_TURN;

		int manaCap = gameState.getManaCapacity();

		if (gameState.isPlayer1Turn) {
			// Human turn: human gets mana, AI cleared
			gameState.player1.setMana(manaCap);
			gameState.player2.setMana(0);

			if (out != null) {
				BasicCommands.setPlayer1Mana(out, gameState.player1);
				BasicCommands.setPlayer2Mana(out, gameState.player2);
				BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
			}
		} else {
			// AI turn: AI gets mana, human cleared
			gameState.player2.setMana(manaCap);
			gameState.player1.setMana(0);

			if (out != null) {
				BasicCommands.setPlayer2Mana(out, gameState.player2);
				BasicCommands.setPlayer1Mana(out, gameState.player1);
				BasicCommands.addPlayer2Notification(out, "Enemy Turn", 2);
			}
		}
	}

}
