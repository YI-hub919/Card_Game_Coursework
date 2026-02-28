import org.junit.Test;
import static org.junit.Assert.*;

import structures.Board;
import structures.basic.Tile;

public class BoardTest {

    @Test
    public void shouldCreate9x5Tiles() {
        Board board = new Board();

        for (int x = 1; x <= Board.BOARD_WIDTH; x++) {
            for (int y = 1; y <= Board.BOARD_HEIGHT; y++) {
                Tile tile = board.getTile(x, y);
                assertNotNull("Tile should not be null at (" + x + "," + y + ")", tile);
                assertEquals(x, tile.getTilex());
                assertEquals(y, tile.getTiley());
            }
        }
    }
}