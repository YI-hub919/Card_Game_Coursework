package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.Board;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		gameState.gameInitalised = true;

		gameState.something = true;

		// Create and store the game board (9x5 grid)
		Board board = new Board();
		gameState.board = board;
		// Render all tiles on the board
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

		for (int x = 1; x <= Board.BOARD_WIDTH; x++) {
			for (int y = 1; y <= Board.BOARD_HEIGHT; y++) {
				BasicCommands.drawTile(out, board.getTile(x, y), 0);
				try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }//simple "laying tiles" effect
			}
		}

		// Short pause after board rendering before avatars appear
		try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }


		//initAvatar(out, gameState);

		// Create avatars and place them on starting tiles
		Tile tile1 = gameState.board.getTile(1, 3);
		Tile tile2 = gameState.board.getTile(9, 3);

		gameState.player1AvatarTile = tile1;
		gameState.player2AvatarTile = tile2;

// Load avatar units
		Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);

		gameState.player1Avatar = humanAvatar;
		gameState.player2Avatar = aiAvatar;

// Initial stats
		int initial_health = 20;
		int initial_attack = 2;
		int initial_robustness = 0;

// Apply stats + position and draw units
		humanAvatar.setHealth(initial_health);
		humanAvatar.setAttack(initial_attack);
		humanAvatar.setRobustness(initial_robustness);
		humanAvatar.setPositionByTile(tile1);
		BasicCommands.drawUnit(out, humanAvatar, tile1);

		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }


		aiAvatar.setHealth(initial_health);
		aiAvatar.setAttack(initial_attack);
		aiAvatar.setRobustness(initial_robustness);
		aiAvatar.setPositionByTile(tile2);
		BasicCommands.drawUnit(out, aiAvatar, tile2);

// Update rendered attack/health after a short delay
		new Thread(() -> {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
			BasicCommands.setUnitHealth(out, aiAvatar, initial_health);

			BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);
			BasicCommands.setUnitAttack(out, aiAvatar, initial_attack);

		}).start();


	}

	// Legacy method kept for existing unit tests (do not remove unless tests are updated)
	public void initAvatar(ActorRef out, GameState gameState) {

		// Ensure board exists for tile lookup (tests may not run full initialize flow)
		if (gameState.board == null) {
			Board board = new Board();
			gameState.board = board;
		}

		// Starting tiles
		Tile tile1 = gameState.board.getTile(1, 2);
		Tile tile2 = gameState.board.getTile(7, 2);

		gameState.player1AvatarTile = tile1;
		gameState.player2AvatarTile = tile2;

		// Load avatar units
		Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);

		gameState.player1Avatar = humanAvatar;
		gameState.player2Avatar = aiAvatar;

		int initial_health = 20;
		int initial_attack = 2;
		int initial_robustness = 0;

		humanAvatar.setHealth(initial_health);
		humanAvatar.setAttack(initial_attack);
		humanAvatar.setRobustness(initial_robustness);
		humanAvatar.setPositionByTile(tile1);

		aiAvatar.setHealth(initial_health);
		aiAvatar.setAttack(initial_attack);
		aiAvatar.setRobustness(initial_robustness);
		aiAvatar.setPositionByTile(tile2);

		// Only issue UI commands when out is available (out is null in some unit tests)
		if (out != null) {
			BasicCommands.drawUnit(out, humanAvatar, tile1);
			BasicCommands.drawUnit(out, aiAvatar, tile2);

			new Thread(() -> {
				try { Thread.sleep(800); } catch (InterruptedException e) { e.printStackTrace(); }
				BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
				BasicCommands.setUnitHealth(out, aiAvatar, initial_health);
				BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);
				BasicCommands.setUnitAttack(out, aiAvatar, initial_attack);
			}).start();
		}
	}

}


