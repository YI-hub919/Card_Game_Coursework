package structures;

import structures.basic.Player;
import structures.basic.Tile;

import java.util.ArrayList;
import structures.basic.Unit;


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

    public Unit player1Avatar; //store the 1st player information

    public Unit player2Avatar;    //store 2ed player information

    public Board board;

    public Tile player1AvatarTile;

    public Tile player2AvatarTile;

    public Tile selectedTile;

    public Unit selectedUnit;

    public void nextRounds() {
        rounds++;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int round) {
        rounds = round;
    }

    public int getManaCapacity() {
        int maxmana = rounds + 1;
        if (maxmana > 9) {
            maxmana = 9;
        }
        return maxmana;
    }

    // Turn control variables
    private boolean endTurnRequested = false;  // End turn button clicked
    public int movingUnitsCount = 0;
    public boolean isPlayer1Turn = true;

    public enum TurnPhase {
        HUMAN_TURN,
        END_TURN_PENDING,
        AI_TURN
    }

    public TurnPhase phase = TurnPhase.HUMAN_TURN;

    public void requestEndTurn() {
        this.endTurnRequested = true;
    }

    public void clearEndTurnRequest() {
        this.endTurnRequested = false;
    }

    public boolean isEndTurnRequested() {
        return this.endTurnRequested;
    }


    public int selectedHandCard = -1; // 0-5, -1 means none selected
    public int nextUnitId = 10; // id for summoned units
    public ArrayList<Unit> summonedUnits = new ArrayList<>();
}

