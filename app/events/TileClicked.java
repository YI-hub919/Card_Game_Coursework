package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

import commands.BasicCommands;

import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Position;

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
public class TileClicked implements EventProcessor{

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

			if (!canSummonHere(gameState, tilex)) {
				BasicCommands.addPlayer1Notification(out, "Cannot summon here", 2);
				gameState.selectedHandCard = -1;
				return;
			}

			// Cannot summon onto an occupied tile
			if (isTileOccupied(gameState, tilex, tiley)) {
				BasicCommands.addPlayer1Notification(out, "Tile is occupied", 2);
				gameState.selectedHandCard = -1;
				return;
			}

			int newId = gameState.nextUnitId++;
			Unit unit = BasicObjectBuilders.loadUnit(card.getUnitConfig(), newId, Unit.class);

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
			try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

			// place + draw
			unit.setPositionByTile(tile);
			BasicCommands.drawUnit(out, unit, tile);

			// update stats (front-end sometimes needs a short delay)
			BasicCommands.setUnitAttack(out, unit, atk);
			BasicCommands.setUnitHealth(out, unit, hp);
			new Thread(() -> {
				try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
				BasicCommands.setUnitAttack(out, unit, atk);
				BasicCommands.setUnitHealth(out, unit, hp);
			}).start();

			// track summoned unit (for occupied checks, later attacks, etc.)
			gameState.summonedUnits.add(unit);

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

	private boolean isTileOccupied(GameState gs, int x, int y) {
		if (gs.player1Avatar != null && gs.player1Avatar.getPosition() != null
				&& gs.player1Avatar.getPosition().getTilex() == x
				&& gs.player1Avatar.getPosition().getTiley() == y) return true;

		if (gs.player2Avatar != null && gs.player2Avatar.getPosition() != null
				&& gs.player2Avatar.getPosition().getTilex() == x
				&& gs.player2Avatar.getPosition().getTiley() == y) return true;

		for (Unit u : gs.summonedUnits) {
			if (u != null && u.getPosition() != null
					&& u.getPosition().getTilex() == x
					&& u.getPosition().getTiley() == y) return true;
		}
		return false;
	}

	private boolean canSummonHere(GameState gs, int tilex) {
		if (gs.isPlayer1Turn) {
			return tilex <= 4;
		} else {
			return tilex >= 6;
		}
	}
}
