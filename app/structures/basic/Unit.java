package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {
	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	int health;
	int attack;
	int robustness;
	@JsonIgnore
	private Player associatedPlayer;

	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		this.health = 20;      //initial health
		this.attack = 2;	//initial attack
		this.robustness = 0;	//initial robustness
		this.associatedPlayer = null;	//initial associated player
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;

	}


	public void setAssociatedPlayer(Player player) {
		this.associatedPlayer = player;}

	public int getHealth() {
		if (this.associatedPlayer != null) {
			return this.associatedPlayer.getHealth();
		}
		return health;
	}

	public void setHealth(int health) {
		if (this.associatedPlayer != null) {
			this.associatedPlayer.setHealth(health);
		} else {
			this.health = health;
		}
	}


	public int getAttack(){
		if (this.associatedPlayer != null) {
			return this.associatedPlayer.getAttack();
		}
		return attack;
	}
	public void setAttack(int attack){
		if (this.associatedPlayer != null) {
		this.associatedPlayer.setAttack(attack); // 同步到玩家对象
	} else {
		this.attack = attack;
	}}
	public int getRobustness() {
			if (this.associatedPlayer != null) {
				return this.associatedPlayer.getRobustness();
			}
		return robustness;
	}
	public void setRobustness(int robustness) {
			if (this.associatedPlayer != null) {
				this.associatedPlayer.setRobustness(robustness); // 同步到玩家对象
			} else {
		this.robustness = robustness;
	}}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}
	
	
}
