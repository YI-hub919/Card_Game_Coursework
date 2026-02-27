package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

import commands.BasicCommands;

import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Position;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		Tile clickedTile = gameState.board.getTile(tilex, tiley);

		if (gameState.selectedTile != null) {
			BasicCommands.drawTile(out, gameState.selectedTile, 0);
		}

		BasicCommands.drawTile(out, clickedTile, 1);
		gameState.selectedTile = clickedTile;

		gameState.selectedUnit = null;

		if (gameState.player1Avatar != null
				&& gameState.player1Avatar.getPosition() != null
				&& gameState.player1Avatar.getPosition().getTilex() == tilex
				&& gameState.player1Avatar.getPosition().getTiley() == tiley) {
			gameState.selectedUnit = gameState.player1Avatar;
		} else if (gameState.player2Avatar != null
				&& gameState.player2Avatar.getPosition() != null
				&& gameState.player2Avatar.getPosition().getTilex() == tilex
				&& gameState.player2Avatar.getPosition().getTiley() == tiley) {
			gameState.selectedUnit = gameState.player2Avatar;
		}

		if (gameState.selectedUnit != null) {
			String who = (gameState.selectedUnit == gameState.player1Avatar) ? "Human Avatar" : "AI Avatar";
			BasicCommands.addPlayer1Notification(out, "Selected Unit: " + who, 2);
		} else {
			BasicCommands.addPlayer1Notification(out, "No unit on this tile", 2);
		}
	}

}
