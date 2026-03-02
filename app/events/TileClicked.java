package events;


import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import commands.BasicCommands;
import java.util.List;

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

		if (gameState.selectedUnit != null
                && gameState.isSameCamp(gameState.selectedUnit)
                && gameState.isPlayer1Turn
                && gameState.phase == GameState.TurnPhase.HUMAN_TURN
                && gameState.selectedUnit.getNotHasAttacked()
                && gameState.selectedUnit.getNotHasMoved()
                && containsTile(gameState.validMoveTiles, clickedTile)) {

			if (gameState.selectedTile != null) {
				BasicCommands.drawTile(out, gameState.selectedTile, 0);
			}

			clearValidMoveHighlights(out, gameState);
			gameState.validMoveTiles.clear();

			BasicCommands.moveUnitToTile(out, gameState.selectedUnit, clickedTile);

			gameState.selectedUnit.setPositionByTile(clickedTile);
			gameState.selectedUnit.setHasMoved(true);  // one move per unit per turn
			gameState.selectedTile = clickedTile;
			gameState.selectedUnit = null;
			BasicCommands.addPlayer1Notification(out, "Moved", 2);

            return;
		}

		if (gameState.selectedTile != null) {
			BasicCommands.drawTile(out, gameState.selectedTile, 0);
		}

		clearValidMoveHighlights(out, gameState);
		if (gameState.validMoveTiles != null) {
			gameState.validMoveTiles.clear();
		}

		BasicCommands.drawTile(out, clickedTile, 1);
		gameState.selectedTile = clickedTile;
		gameState.selectedUnit = gameState.getUnitOnTile(clickedTile);

		if (gameState.selectedUnit != null
				&& gameState.isSameCamp(gameState.selectedUnit)
				&& gameState.isPlayer1Turn
				&& gameState.phase == GameState.TurnPhase.HUMAN_TURN
				&& gameState.selectedUnit.getNotHasAttacked()
				&& gameState.selectedUnit.getNotHasMoved()) {

			gameState.updateValidMoveTiles();
			for (Tile t : gameState.validMoveTiles) {
				BasicCommands.drawTile(out, t, 1);
			}
		}

		if (gameState.selectedUnit != null) {
			String label = getUnitTypeLabel(gameState.getUnitType(gameState.selectedUnit));
			BasicCommands.addPlayer1Notification(out, "Selected Unit: " + label, 2);
		} else {
			BasicCommands.addPlayer1Notification(out, "No unit on this Tile", 2);
		}
	}

	private void clearValidMoveHighlights(ActorRef out, GameState gameState) {
		if (gameState.validMoveTiles == null) {
            return;
        }
		for (Tile t : gameState.validMoveTiles) {
			BasicCommands.drawTile(out, t, 0);
		}
	}

	private boolean containsTile(List<Tile> list, Tile tile) {
		if (list == null || tile == null) return false;
		for (Tile t: list) {
			if ((t.getTilex() == tile.getTilex() && t.getTiley() == tile.getTiley())) {
                return true;
            }
		}
		return false;
	}

	private String getUnitTypeLabel(GameState.UnitType type) {
		if (type == null)  {
            return "Unknown";
        }

		switch (type) {
			case HUMAN_AVATAR:    return "Human Player Avatar";
			case HUMAN_SUMMONED:  return "Human Player Summon Unit";
			case AI_AVATAR:       return "AI Player Avatar";
			case AI_SUMMONED:     return "AI Player Summon Unit";
			default:              return "Unknown";
		}
	}
}
