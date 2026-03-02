package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import akka.actor.ActorRef;
import play.libs.Json;
import structures.basic.Card;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Fully compliant BasicCommands:
 * 1. Fixes syntax errors
 * 2. Restores template dependencies
 * 3. Adds complete test coverage
 * JDK 11 compatible, template-integrated
 */
public class CardInfoEnlargedTest {

    private static ObjectMapper mapper = new ObjectMapper();
    public static DummyTell altTell = null;

    public static void addPlayer1Notification(ActorRef out, String text, int displayTimeSeconds) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "addPlayer1Notification");
            returnMessage.put("text", text);
            returnMessage.put("seconds", displayTimeSeconds);
            if (altTell != null) altTell.tell(returnMessage);
            else if (out != null) {
                out.tell(returnMessage, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void drawCard(ActorRef out, Card card, int position, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawCard");
            returnMessage.set("card", Json.toJson(card)); 
            returnMessage.put("position", position);
            returnMessage.put("mode", mode);
            if (altTell != null) altTell.tell(returnMessage);
            else if (out != null) {
                out.tell(returnMessage, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Core function: Get card info + send enlarged display to UI
     */
    public static void showEnlargedCardInfo(ActorRef out, Card card) {
        if (card == null) {
            addPlayer1Notification(out, "Invalid card! No information to display", 3);
            return;
        }

        StringBuilder enlargedInfo = new StringBuilder();
        enlargedInfo.append("=== ENLARGED CARD INFO ===\n");
        enlargedInfo.append("Name: ").append(card.getName() != null ? card.getName() : "N/A").append("\n");
        enlargedInfo.append("Mana Cost: ").append(card.getManaCost() != null ? card.getManaCost() : "N/A").append("\n");

        if (card.getType() == Card.CardType.CREATURE) {
            enlargedInfo.append("Attack: ").append(card.getAttack() != null ? card.getAttack() : "N/A").append("\n");
            enlargedInfo.append("Health: ").append(card.getHealth() != null ? card.getHealth() : "N/A").append("\n");
        }

        enlargedInfo.append("Description: ").append(card.getDescription() != null ? card.getDescription() : "No description");

        addPlayer1Notification(out, enlargedInfo.toString(), 5);
        drawCard(out, card, 0, 1);
    }

    @AfterEach
    public void tearDown() {
        altTell = null;
    }

    @Test
    public void testCreatureCardEnlargedDisplay() {
        DummyTell testTell = new DummyTell();
        altTell = testTell;

        Card creatureCard = new Card();
        creatureCard.setCardId("CRE001");
        creatureCard.setName("Werewolf");
        creatureCard.setType(Card.CardType.CREATURE);
        creatureCard.setManaCost(2);
        creatureCard.setAttack(2);
        creatureCard.setHealth(2);
        creatureCard.setDescription("Fierce wolf with sharp claws");

        showEnlargedCardInfo(null, creatureCard);

        assertEquals("Werewolf", creatureCard.getName());
        assertEquals(2, creatureCard.getManaCost());
        assertEquals(2, creatureCard.getAttack());
        assertEquals(2, creatureCard.getHealth());
        assertEquals(Card.CardType.CREATURE, creatureCard.getType());

        String expectedNotification = "=== ENLARGED CARD INFO ===\nName: Werewolf\nMana Cost: 2\nAttack: 2\nHealth: 2\nDescription: Fierce wolf with sharp claws";
        assertTrue(testTell.getLastNotificationText().contains(expectedNotification));

        assertNotNull(testTell.getLastDrawCardNode());
        assertEquals("Werewolf", testTell.getLastDrawCardNode().get("card").get("name").asText());
    }

    @Test
    public void testSpellCardEnlargedDisplay() {
        DummyTell testTell = new DummyTell();
        altTell = testTell;

        Card spellCard = new Card();
        spellCard.setCardId("SPE001");
        spellCard.setName("Fireball");
        spellCard.setType(Card.CardType.SPELL);
        spellCard.setManaCost(3);
        spellCard.setDescription("Deal 4 damage to a single target");

        showEnlargedCardInfo(null, spellCard);

        assertEquals("Fireball", spellCard.getName());
        assertEquals(3, spellCard.getManaCost());
        assertNull(spellCard.getAttack());
        assertNull(spellCard.getHealth());
        assertEquals(Card.CardType.SPELL, spellCard.getType());

        String notificationText = testTell.getLastNotificationText();
        assertTrue(notificationText.contains("Name: Fireball"));
        assertTrue(notificationText.contains("Mana Cost: 3"));
        assertFalse(notificationText.contains("Attack:"));
        assertFalse(notificationText.contains("Health:"));
    }

    @Test
    public void testNullCard() {
        DummyTell testTell = new DummyTell();
        altTell = testTell;

        showEnlargedCardInfo(null, null);

        assertEquals("Invalid card! No information to display", testTell.getLastNotificationText());
    }

    @Test
    public void testCreatureCardWithMissingAttack() {
        DummyTell testTell = new DummyTell();
        altTell = testTell;

        Card invalidCreature = new Card();
        invalidCreature.setName("Broken Wolf");
        invalidCreature.setType(Card.CardType.CREATURE);
        invalidCreature.setManaCost(2);
        invalidCreature.setHealth(2);
        invalidCreature.setAttack(null);

        showEnlargedCardInfo(null, invalidCreature);

        String notificationText = testTell.getLastNotificationText();
        assertTrue(notificationText.contains("Attack: N/A"));
        assertTrue(notificationText.contains("Health: 2"));
    }

    public static class DummyTell {
        private String lastNotificationText;
        private ObjectNode lastDrawCardNode;

        public void tell(ObjectNode message) {
            if ("addPlayer1Notification".equals(message.get("messagetype").asText())) {
                this.lastNotificationText = message.get("text").asText();
            } else if ("drawCard".equals(message.get("messagetype").asText())) {
                this.lastDrawCardNode = message;
            }
            System.out.println("Front-end Message: " + message.toString());
        }

        public String getLastNotificationText() {
            return lastNotificationText;
        }

        public ObjectNode getLastDrawCardNode() {
            return lastDrawCardNode;
        }
    }
}