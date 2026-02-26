package structures.basic;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int health;
	int mana;
	int attack;
	int robustness;
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		this.attack = 2;
		this.robustness = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	public int getAttack(){return attack;}
	public int getRobustness(){return robustness;}
	public void setAttack(int attack) { this.attack = attack;}
	public void setRobustness(int robustness) {this.robustness = robustness;}
}
