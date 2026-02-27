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
		for (int x = 1; x <= Board.BOARD_WIDTH; x++) {
			for (int y = 1; y <= Board.BOARD_HEIGHT; y++) {
				BasicCommands.drawTile(out, board.getTile(x, y), 0);
				try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }//simple "laying tiles" effect
			}
		}

		initAvatar(out, gameState);


	}

	public void initAvatar(ActorRef out, GameState gameState){
//	set the initial avatar position
		// Get starting tiles for both avatars
		Tile tile1 = gameState.board.getTile(1, 3);
		Tile tile2 = gameState.board.getTile(9, 3);
		// Store current tile references in GameState
		gameState.player1AvatarTile = tile1;
		gameState.player2AvatarTile = tile2;

		//create humanAvatar and aiAvatar unit
		Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
		//record the avatar information in GameState
		gameState.player1Avatar = humanAvatar;
		gameState.player2Avatar = aiAvatar;
		//set the initial health,attack,robustness of each avatar
		int initial_health = 20;
		int initial_attack = 2;
		int initial_robustness = 0;
		//set the initial information and draw the avatar units
		humanAvatar.setHealth(initial_health);
		humanAvatar.setAttack(initial_attack);
		humanAvatar.setRobustness(initial_robustness);
		humanAvatar.setPositionByTile(tile1);
		BasicCommands.drawUnit(out, humanAvatar, tile1);
		aiAvatar.setHealth(initial_health);
		aiAvatar.setAttack(initial_attack);
		aiAvatar.setRobustness(initial_robustness);
		aiAvatar.setPositionByTile(tile2);
		BasicCommands.drawUnit(out, aiAvatar, tile2);

		new Thread(() -> {
			try {
				// 500ms delay
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// finished
			//send the information to front-end
//			BasicCommands.addPlayer1Notification(out, "setUnitHealth", initial_health);
			BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
			BasicCommands.setUnitHealth(out, aiAvatar, initial_health);

//			BasicCommands.addPlayer1Notification(out, "setUnitAttack", initial_attack);
			BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);
			BasicCommands.setUnitAttack(out, aiAvatar, initial_attack);

		}).start();






		//try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);
	}

}


