package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;

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

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 5; j++) {
                gameState.boardTile.add(BasicObjectBuilders.loadTile(i, j));
            }
        }

        for(Tile tile: gameState.boardTile){
            BasicCommands.drawTile(out, tile, 0);
            try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
        }

        BasicCommands.addPlayer1Notification(out, "Game Start", 3);
        Player humanPlayer = new Player(20, 0);
        Player aiPlayer = new Player(20, 0);

        gameState.player1 = humanPlayer;
        gameState.player2 = aiPlayer;

        humanPlayer.setCardDeck(OrderedCardLoader.getPlayer1Cards(2));
        aiPlayer.setCardDeck(OrderedCardLoader.getPlayer2Cards(2));

        BasicCommands.setPlayer1Health(out, gameState.player1);
        BasicCommands.setPlayer2Health(out, gameState.player2);
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        BasicCommands.setPlayer2Mana(out, gameState.player2);

        gameState.nextRounds();
        // Round 1
        int roundNum = gameState.getRounds();
        int roundMana = gameState.getManaCapacity();

        BasicCommands.addPlayer1Notification(out, (String.format("Human Player Round %d", roundNum)), 2);
        humanPlayer.setMana(roundMana);
        BasicCommands.setPlayer1Mana(out, gameState.player1);

        // first draw 3 cards
        for (int i = 0; i < 3; i++) {
            Player.drawCard(out, humanPlayer);
        }

        try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}

        humanPlayer.setMana(0);
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

        BasicCommands.addPlayer2Notification(out, "AI Player Round 1", 2);
        aiPlayer.setMana(roundMana);
        try {Thread.sleep(2500);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setPlayer2Mana(out, gameState.player2);
        aiPlayer.setMana(roundMana);
        BasicCommands.setPlayer2Mana(out, gameState.player2);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}


		// User 1 makes a change
//		CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);

	}

}


