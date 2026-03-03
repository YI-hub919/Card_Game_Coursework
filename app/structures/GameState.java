package structures;

import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.Tile;

import java.util.ArrayList;
import java.util.List;
import structures.basic.Unit;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 */

public class GameState {

    public enum UnitType {
        HUMAN_AVATAR,
        HUMAN_SUMMONED,
        AI_AVATAR,
        AI_SUMMONED
    }

    public boolean gameInitialised = false;

    public boolean something = false;

    private int rounds = 0;

    public Player player1 = new Player();
    public Player player2 = new Player();

    public Avatar player1Avatar;
    public Avatar player2Avatar;

    public List<Unit> player1SummonedUnits = new ArrayList<>();
    public List<Unit> player2SummonedUnits = new ArrayList<>();

    public Board board;

    public Tile player1AvatarTile;
    public Tile player2AvatarTile;

    public Tile selectedTile;
    public Unit selectedUnit;

    public List<Tile> validMoveTiles = new ArrayList<>();
    public static final int UNIT_MOVE_RANGE = 2;

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

    public boolean isPlayer1Turn = true;      // True if it's Player 1's turn

    public enum TurnPhase {
        HUMAN_TURN,
        END_TURN_PENDING,
        AI_TURN
    }

    public TurnPhase phase = TurnPhase.HUMAN_TURN;

    // End turn flag should only be written via these methods
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

    public void updateValidMoveTiles() {
        
        validMoveTiles.clear();
        if (selectedUnit == null) {
            return;
        }
        validMoveTiles.addAll(getValidMoveTiles(selectedUnit));
    }

    public List<Tile> getValidMoveTiles(Unit unit) {

        List<Tile> result = new ArrayList<>();
        if (unit == null || unit.getPosition() == null) {
            return result;
        }

        int ux = unit.getPosition().getTilex();
        int uy = unit.getPosition().getTiley();

        for (int x = 1; x <= Board.BOARD_WIDTH; x++) {
            for (int y = 1; y <= Board.BOARD_HEIGHT; y++) {
                int dist = Math.abs(x - ux) + Math.abs(y - uy);
                if (dist == 0 || dist > UNIT_MOVE_RANGE) {
                    continue;
                }

                Tile t = board.getTile(x, y);
                if (isTileOccupied(t)) {
                    continue;
                }
                result.add(t);
            }
        }

        return result;
    }

    private boolean isTileOccupied(Tile tile) {
        return getUnitOnTile(tile) != null;
    }

    public Unit getUnitOnTile(Tile tile) {
        if (tile == null) {
            return null;
        }
        for (Unit unit : getAllUnits()) {
            if ((unit.getPosition() != null
                    && unit.getPosition().getTilex() == tile.getTilex()
                    && unit.getPosition().getTiley() == tile.getTiley())) {
                return unit;
            }
        }
        return null;
    }

    public List<Unit> getAllUnits() {
        List<Unit> list = new ArrayList<>();

        if (player1Avatar != null) {
            list.add(player1Avatar);
        }

        if (player2Avatar != null) {
            list.add(player2Avatar);
        }

        list.addAll(player1SummonedUnits);
        list.addAll(player2SummonedUnits);

        return list;
    }

    public boolean isSameCamp(Unit unit) {
        if (unit == null) {
            return false;
        }

        if (isPlayer1Turn) {
            return unit == player1Avatar || player1SummonedUnits.contains(unit);
        } else {
            return unit == player2Avatar || player2SummonedUnits.contains(unit);
        }
    }

    public UnitType getUnitType(Unit unit) {
        if (unit == null) {
            return null;
        }

        if (unit == player1Avatar) {
            return UnitType.HUMAN_AVATAR;
        }

        if (player1SummonedUnits.contains(unit)) {
            return UnitType.HUMAN_SUMMONED;
        }

        if (unit == player2Avatar) {
            return UnitType.AI_AVATAR;
        }

        if (player2SummonedUnits.contains(unit)) {
            return UnitType.AI_SUMMONED;
        }

        return null;
    }
}

