package structures;

import structures.basic.Tile;
import utils.BasicObjectBuilders;

public class Board {
    public static final int BOARD_WIDTH = 9;
    public static final int BOARD_HEIGHT = 5;

    private final Tile[][] tiles = new Tile[BOARD_WIDTH][BOARD_HEIGHT];

    public Board() {
        for (int x = 1; x <= BOARD_WIDTH; x++) {
            for (int y = 1; y <= BOARD_HEIGHT; y++) {
                tiles[x - 1][y - 1] = BasicObjectBuilders.loadTile(x, y);
            }
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x - 1][y - 1];
    }
}