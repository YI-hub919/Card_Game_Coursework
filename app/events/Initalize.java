package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

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
		// hello this is a change

		gameState.gameInitalised = true;

		gameState.something = true;

		initAvatar(out, gameState);


	}
	public void initAvatar(ActorRef out, GameState gameState){

		Tile tile1 = BasicObjectBuilders.loadTile(1, 2);
		Tile tile2 = BasicObjectBuilders.loadTile(7, 2);
//		BasicCommands.drawTile(out, tile, 0);
//		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}



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
		BasicCommands.drawUnit(out, humanAvatar, tile1);
		aiAvatar.setHealth(initial_health);
		aiAvatar.setAttack(initial_attack);
		aiAvatar.setRobustness(initial_robustness);
		aiAvatar.setPositionByTile(tile2);
		BasicCommands.drawUnit(out, aiAvatar, tile2);
		//delay
		new Thread(() -> {
			try {
				// 500ms delay
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// finished
			BasicCommands.addPlayer1Notification(out, "setUnitHealth", initial_health);
			BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
			BasicCommands.setUnitHealth(out, aiAvatar, initial_health);

			BasicCommands.addPlayer1Notification(out, "setUnitAttack", initial_attack);
			BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);
			BasicCommands.setUnitAttack(out, aiAvatar, initial_attack);

		}).start();

		//humanAvatar.associatedPlayer(player1);


//		BasicCommands.addPlayer1Notification(out, "setUnitHealth", initial_health);
//		BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
//
//		BasicCommands.addPlayer1Notification(out, "setUnitAttack", initial_attack);
//		BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);





		//try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);
	}

}


