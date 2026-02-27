package structures;

import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

    public ArrayList<Tile> boardTile = new ArrayList<>();

	public boolean gameInitalised = false;
	
	public boolean something = false;

    private int rounds = 0;

    public Player player1 = new Player();
    public Player player2 = new Player();



    public void nextRounds() {
        rounds++;
    }

    public int getRounds() {
        return rounds;
    }

    public int getManaCapacity() {
        int mana = rounds + 1;
        if (mana > 9) {
            mana = 9;
        }
        return mana;
    }




}
