package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;

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
		// Reset end turn request
		gameState.endTurnRequested = false;

		// Increase round count
		gameState.isPlayer1Turn = !gameState.isPlayer1Turn;

		// Only increase rounds when a new Player 1 turn starts
		if (gameState.isPlayer1Turn) {
			gameState.nextRounds();
		}

		// Update phase
		gameState.phase = gameState.isPlayer1Turn ?
				GameState.TurnPhase.HUMAN_TURN :
				GameState.TurnPhase.AI_TURN;

		if (!gameState.isPlayer1Turn) {

			if (gameState.selectedTile != null) {
				BasicCommands.drawTile(out, gameState.selectedTile, 0);
				gameState.selectedTile = null;
			}

			if (gameState.validMoveTiles != null) {
				for (Tile t : gameState.validMoveTiles) {
					BasicCommands.drawTile(out, t, 0);
				}
				gameState.validMoveTiles.clear();
			}

			gameState.selectedUnit = null;
		}

		// Update mana for both players based on round
		int mana = gameState.getManaCapacity();

		if (gameState.isPlayer1Turn) {
			gameState.player1.setMana(mana);

			if (out != null) {
				BasicCommands.setPlayer1Mana(out, gameState.player1);
				BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
			}
		} else {
			gameState.player2.setMana(mana);

			if (out != null) {
				BasicCommands.setPlayer2Mana(out, gameState.player2);
				BasicCommands.addPlayer1Notification(out, "Enemy Turn", 2);
			}
		}
	}

}
