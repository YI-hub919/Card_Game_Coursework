package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
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
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        gameState.gameInitalised = true;
        gameState.something = true;

        // Create and store the game board (9x5 grid) using Board (dev style)
        Board board = new Board();
        gameState.board = board;

        // Render all tiles on the board with a "laying tiles" effect
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        for (int x = 1; x <= Board.BOARD_WIDTH; x++) {
            for (int y = 1; y <= Board.BOARD_HEIGHT; y++) {
                BasicCommands.drawTile(out, board.getTile(x, y), 0);
                try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

        // Short pause after board rendering before avatars appear
        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

        // Create avatars and place them on starting tiles
        Tile tile1 = gameState.board.getTile(2, 3);
        Tile tile2 = gameState.board.getTile(8, 3);

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
            try { Thread.sleep(800); } catch (InterruptedException e) { e.printStackTrace(); }
            BasicCommands.setUnitHealth(out, humanAvatar, initial_health);
            BasicCommands.setUnitHealth(out, aiAvatar, initial_health);
            BasicCommands.setUnitAttack(out, humanAvatar, initial_attack);
            BasicCommands.setUnitAttack(out, aiAvatar, initial_attack);
        }).start();

        // Initialize players
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

        // Round 1 setup
        gameState.nextRounds();
        int roundNum = gameState.getRounds();
        int roundMana = gameState.getManaCapacity();

        BasicCommands.addPlayer1Notification(out, String.format("Human Player Round %d", roundNum), 2);
        humanPlayer.setMana(roundMana);
        BasicCommands.setPlayer1Mana(out, gameState.player1);

        // Human Draw 3 starting cards
        for (int i = 0; i < 3; i++) {
            Player.drawCard(out, humanPlayer);
        }

        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
        humanPlayer.setMana(0);
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }

        BasicCommands.addPlayer2Notification(out, String.format("AI Player Round %d", roundNum), 2);
        aiPlayer.setMana(roundMana);
        BasicCommands.setPlayer2Mana(out, gameState.player2);

        // Human Draw 3 starting cards
        for (int i = 0; i < 3; i++) {
            Player.drawCard(out, humanPlayer);
        }

        try { Thread.sleep(2500); } catch (InterruptedException e) { e.printStackTrace(); }
        aiPlayer.setMana(0);
        BasicCommands.setPlayer2Mana(out, gameState.player2);
        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
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


