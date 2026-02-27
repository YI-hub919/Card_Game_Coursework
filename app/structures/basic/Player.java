package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.List;

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

    int nextCardNum = 0;

    private List<Card> cardDeck = new ArrayList<>();
    private List<Card> cardInHand = new ArrayList<>();

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
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
        if (health < 0) {
            health = 0;
        }
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}

    public void setCardDeck(List<Card> cards) {
        cardDeck = cards;
    }

    public List<Card> getCardInHand() {
        return cardInHand;
    }

    public static void drawCard(ActorRef out, Player player) {
        if (player.nextCardNum >= player.cardDeck.size()) {
            System.out.println("No more cards in deck");
            return;
        }

        Card newDrawnCard = player.cardDeck.get(player.nextCardNum);
        player.nextCardNum++;

        if (player.cardInHand.size() >= 6) {
            System.out.println("Too many cards in hand");
            return;
        }

        int cardPosition = player.cardInHand.size();

        BasicCommands.drawCard(out, newDrawnCard, cardPosition, 0);
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

        player.cardInHand.add(newDrawnCard);
    }
	
}
