package events;


import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import commands.BasicCommands;
import java.util.List;

import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.basic.EffectAnimation;

import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.basic.EffectAnimation;

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
public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		if (gameState.selectedHandCard != -1) {

			Tile tile = gameState.board.getTile(tilex, tiley);

			var hand = gameState.player1.getCardInHand();

			if (gameState.selectedHandCard >= hand.size()) {
				gameState.selectedHandCard = -1;
				return;
			}

			var card = hand.get(gameState.selectedHandCard);

			if (gameState.player1.getMana() < card.getManacost()) {
				BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
				gameState.selectedHandCard = -1;
				return;
			}

			if (!card.isCreature()) {
				gameState.selectedHandCard = -1;
				return;
			}

			// Cannot summon onto an occupied tile
			if (isTileOccupied(gameState, tilex, tiley)) {
				BasicCommands.addPlayer1Notification(out, "Tile is occupied", 2);
				gameState.selectedHandCard = -1;
				return;
			}

			// Check if the tile is in the valid move tiles

			int newId = gameState.nextUnitId++;
			Unit unit = BasicObjectBuilders.loadUnit(card.getUnitConfig(), newId, Unit.class);
			unit.setUnitName(card.getCardname());

			int atk = card.getBigCard().getAttack();
			int hp  = card.getBigCard().getHealth();
			unit.setAttack(atk);
			unit.setHealth(hp);

			// spend mana
			gameState.player1.setMana(gameState.player1.getMana() - card.getManacost());
			BasicCommands.setPlayer1Mana(out, gameState.player1);

			EffectAnimation summonFX = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
			if (summonFX != null) {
				BasicCommands.playEffectAnimation(out, summonFX, tile);
			}
			if (out != null) {
				try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
			}
			// place + draw
			unit.setPositionByTile(tile);
			BasicCommands.drawUnit(out, unit, tile);

			// update stats (front-end sometimes needs a short delay)
			BasicCommands.setUnitAttack(out, unit, atk);
			BasicCommands.setUnitHealth(out, unit, hp);
			if (out != null) {
				new Thread(() -> {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					BasicCommands.setUnitAttack(out, unit, atk);
					BasicCommands.setUnitHealth(out, unit, hp);
				}).start();
			}

			// track summoned unit (for occupied checks, later attacks, etc.)
			gameState.player1SummonedUnits.add(unit);

			hand.remove(gameState.selectedHandCard);

			// clear slots 1..6
			for (int slot = 1; slot <= 6; slot++) {
				BasicCommands.drawCard(out, null, slot, 0);
			}

			// redraw current hand into slots 1..handSize
			for (int i = 0; i < hand.size(); i++) {
				BasicCommands.drawCard(out, hand.get(i), i + 1, 0);
			}

			gameState.selectedHandCard = -1;
			return;
		}

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
			if (gameState.selectedUnit == gameState.player1Avatar) {
				BasicCommands.addPlayer1Notification(out, "Selected: P1 Avatar", 2);

			} else if (gameState.selectedUnit == gameState.player2Avatar) {
				BasicCommands.addPlayer1Notification(out, "Selected: P2 Avatar", 2);

			} else {
				String name = gameState.selectedUnit.getUnitName();
				if (name == null) {
					name = "Unit";
				}

				BasicCommands.addPlayer1Notification(
						out,
						"Selected: " + name + " ("
								+ gameState.selectedUnit.getAttack()
								+ "/"
								+ gameState.selectedUnit.getHealth()
								+ ")",
						2
				);
			}
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

	private boolean isTileOccupied(GameState gs, int x, int y) {
		return gs.getUnitOnTile(gs.board.getTile(x, y)) != null;
	}
}
