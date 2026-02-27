package structures;
import structures.basic.Unit;
import structures.basic.Tile;
/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	public Unit player1Avatar; //store the 1st player information

	public Unit player2Avatar;	//store 2ed player information

	public Board board;

	public Tile player1AvatarTile;

	public Tile player2AvatarTile;

	public Tile selectedTile;
}
