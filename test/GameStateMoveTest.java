import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import structures.Board;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

import java.util.List;

public class GameStateMoveTest {

    private GameState gameState;

    private Unit makeUnit(int tilex, int tiley) {
        Unit unit = new Unit();
        unit.setPosition(new Position(0, 0, tilex, tiley));
        return unit;
    }

    private Avatar makeAvatar(int tilex, int tiley) {
        Avatar avatar = new Avatar();
        avatar.setPosition(new Position(0, 0, tilex, tiley));
        return avatar;
    }

    @Before
    public void setUp() {
        gameState = new GameState();
        gameState.board = new Board();
    }

    @Test
    public void testGetValidMoveTiles_nullUnit() {
        List<Tile> result = gameState.getValidMoveTiles(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetValidMoveTiles_unitWithNullPosition() {
        Unit unit = new Unit();
        List<Tile> result = gameState.getValidMoveTiles(unit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetValidMoveTiles_Unit_hasCorrectMoveTilesCount() {
        Unit unit = makeUnit(5, 3);
        gameState.player1SummonedUnits.add(unit);
        List<Tile> tiles = gameState.getValidMoveTiles(unit);

        assertEquals(12, tiles.size());
    }

    @Test
    public void testGetValidMoveTiles_Unit_hasFewerMoveTilesCount() {
        Unit unit = makeUnit(1, 1);
        gameState.player1SummonedUnits.add(unit);
        List<Tile> tiles = gameState.getValidMoveTiles(unit);

        assertEquals(5, tiles.size());
    }

    @Test
    public void testGetValidMoveTiles_excludesSelfTile() {
        Unit unit = makeUnit(3, 2);
        gameState.player1SummonedUnits.add(unit);
        List<Tile> tiles = gameState.getValidMoveTiles(unit);
        for (Tile t : tiles) {
            assertFalse(
                    "Own tile should not be in valid move tiles",
                    t.getTilex() == 3 && t.getTiley() == 2
            );
        }
    }

    @Test
    public void testGetValidMoveTiles_excludesOccupiedTiles() {
        Unit mover = makeUnit(5, 3);
        Unit blocker = makeUnit(4, 3);
        gameState.player1SummonedUnits.add(mover);
        gameState.player1SummonedUnits.add(blocker);

        List<Tile> tiles = gameState.getValidMoveTiles(mover);
        for (Tile t : tiles) {
            assertFalse(
                    "Occupied tile (4,3) should not be a valid move tile",
                    t.getTilex() == 4 && t.getTiley() == 3
            );
        }
    }

    @Test
    public void testGetValidMoveTiles_tilesMustBeWithinMoveRange() {
        Unit unit = makeUnit(5, 3);
        gameState.player1SummonedUnits.add(unit);
        List<Tile> tiles = gameState.getValidMoveTiles(unit);
        for (Tile t : tiles) {
            int dist = Math.abs(t.getTilex() - 5) + Math.abs(t.getTiley() - 3);
            assertTrue(
                    "Tile (" + t.getTilex() + "," + t.getTiley() + ") exceeds move range",
                    dist >= 1 && dist <= GameState.UNIT_MOVE_RANGE
            );
        }
    }

    @Test
    public void testUpdateValidMoveTiles_noSelectedUnit_clearsList() {
        gameState.validMoveTiles.add(gameState.board.getTile(1, 1));
        gameState.selectedUnit = null;
        gameState.updateValidMoveTiles();
        assertTrue(gameState.validMoveTiles.isEmpty());
    }

    @Test
    public void testUpdateValidMoveTiles_withSelectedUnit_populatesList() {
        Unit unit = makeUnit(5, 3);
        gameState.player1SummonedUnits.add(unit);
        gameState.selectedUnit = unit;
        gameState.updateValidMoveTiles();
        assertFalse(gameState.validMoveTiles.isEmpty());
        assertEquals(gameState.getValidMoveTiles(unit).size(), gameState.validMoveTiles.size());
    }

    @Test
    public void testGetUnitOnTile_nullTile_returnsNull() {
        assertNull(gameState.getUnitOnTile(null));
    }

    @Test
    public void testGetUnitOnTile_emptyBoard_returnsNull() {
        Tile tile = gameState.board.getTile(3, 3);
        assertNull(gameState.getUnitOnTile(tile));
    }

    @Test
    public void testGetUnitOnTile_unitPresent_returnsUnit() {
        Unit unit = makeUnit(3, 3);
        gameState.player1SummonedUnits.add(unit);

        Tile tile = gameState.board.getTile(3, 3);
        assertEquals(unit, gameState.getUnitOnTile(tile));
    }

    @Test
    public void testGetUnitOnTile_differentTile_returnsNull() {
        Unit unit = makeUnit(3, 3);
        gameState.player1SummonedUnits.add(unit);

        Tile tile = gameState.board.getTile(4, 4);
        assertNull(gameState.getUnitOnTile(tile));
    }

    @Test
    public void testGetUnitOnTile_avatarOnTile_returnsAvatar() {
        Avatar avatar = makeAvatar(1, 1);
        gameState.player1Avatar = avatar;

        Tile tile = gameState.board.getTile(1, 1);
        assertEquals(avatar, gameState.getUnitOnTile(tile));
    }

    @Test
    public void testGetAllUnits_noUnits_returnsEmpty() {
        assertTrue(gameState.getAllUnits().isEmpty());
    }

    @Test
    public void testGetAllUnits_includesAllCategories() {
        Avatar a1 = makeAvatar(1, 1);
        Avatar a2 = makeAvatar(9, 5);
        Unit u1 = makeUnit(2, 2);
        Unit u2 = makeUnit(8, 4);

        gameState.player1Avatar = a1;
        gameState.player2Avatar = a2;
        gameState.player1SummonedUnits.add(u1);
        gameState.player2SummonedUnits.add(u2);

        List<Unit> all = gameState.getAllUnits();

        assertEquals(4, all.size());
        assertTrue(all.contains(a1));
        assertTrue(all.contains(a2));
        assertTrue(all.contains(u1));
        assertTrue(all.contains(u2));
    }

    @Test
    public void testGetAllUnits_onlyAvatars_returnsBoth() {
        gameState.player1Avatar = makeAvatar(1, 1);
        gameState.player2Avatar = makeAvatar(9, 5);

        List<Unit> all = gameState.getAllUnits();
        assertEquals(2, all.size());
    }

    @Test
    public void testIsSameCamp_nullUnit_returnsFalse() {
        assertFalse(gameState.isSameCamp(null));
    }

    @Test
    public void testIsSameCamp_player1Turn_player1AvatarIsAlly() {
        Avatar avatar = makeAvatar(1, 1);
        gameState.player1Avatar = avatar;
        gameState.isPlayer1Turn = true;

        assertTrue(gameState.isSameCamp(avatar));
    }

    @Test
    public void testIsSameCamp_player1Turn_player1SummonedUnitIsAlly() {
        Unit unit = makeUnit(2, 2);
        gameState.player1SummonedUnits.add(unit);
        gameState.isPlayer1Turn = true;

        assertTrue(gameState.isSameCamp(unit));
    }

    @Test
    public void testIsSameCamp_player1Turn_player2UnitIsEnemy() {
        Unit unit = makeUnit(8, 4);
        gameState.player2SummonedUnits.add(unit);
        gameState.isPlayer1Turn = true;

        assertFalse(gameState.isSameCamp(unit));
    }

    @Test
    public void testIsSameCamp_player2Turn_player2AvatarIsAlly() {
        Avatar avatar = makeAvatar(9, 5);
        gameState.player2Avatar = avatar;
        gameState.isPlayer1Turn = false;

        assertTrue(gameState.isSameCamp(avatar));
    }

    @Test
    public void testIsSameCamp_player2Turn_player1UnitIsEnemy() {
        Unit unit = makeUnit(2, 2);
        gameState.player1SummonedUnits.add(unit);
        gameState.isPlayer1Turn = false;

        assertFalse(gameState.isSameCamp(unit));
    }
}
