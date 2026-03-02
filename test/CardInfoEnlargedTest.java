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
 * Fully compliant CardInfoEnlarged:
 * 1. Fixes syntax errors
 * 2. Restores template dependencies
 * 3. Adds complete test coverage
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
            System.err.println("Failed to send player1 notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void drawCard(ActorRef out, Card card, int position, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawCard");

            ObjectNode cardNode = Json.newObject();
            if (card != null) {
                cardNode.put("cardId", card.getCardId() != null ? card.getCardId() : "N/A");
                cardNode.put("name", card.getName() != null ? card.getName() : "N/A");
                cardNode.put("manaCost", card.getManaCost());
                cardNode.put("type", card.getType() != null ? card.getType().name() : "N/A");
                if (card.getType() == Card.CardType.CREATURE) {
                    cardNode.put("attack", card.getAttack());
                    cardNode.put("health", card.getHealth());
                }
                cardNode.put("description", card.getDescription() != null ? card.getDescription() : "No description");
            }
            returnMessage.set("card", cardNode);
            
            returnMessage.put("position", position);
            returnMessage.put("mode", mode);
            if (altTell != null) altTell.tell(returnMessage);
            else if (out != null) {
                out.tell(returnMessage, out);
            }
        } catch (Exception e) {
            System.err.println("Failed to send drawCard message: " + e.getMessage());
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
        enlargedInfo.append("Mana Cost: ").append(card.getManaCost()).append("\n");

        if (card.getType() == Card.CardType.CREATURE) {
            enlargedInfo.append("Attack: ").append(card.getAttack()).append("\n");
            enlargedInfo.append("Health: ").append(card.getHealth()).append("\n");
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
        String actualNotification = testTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertEquals(expectedNotification, actualNotification);

        assertNotNull(testTell.getLastDrawCardNode());
        ObjectNode cardNode = (ObjectNode) testTell.getLastDrawCardNode().get("card");
        assertNotNull(cardNode, "Card JSON node should not be null");
        assertEquals("Werewolf", cardNode.get("name").asText());
        assertEquals(0, testTell.getLastDrawCardNode().get("position").asInt());
        assertEquals(1, testTell.getLastDrawCardNode().get("mode").asInt());
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
        assertEquals(0, spellCard.getAttack()); 
        assertEquals(0, spellCard.getHealth());
        assertEquals(Card.CardType.SPELL, spellCard.getType());

        String notificationText = testTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertTrue(notificationText.contains("Name: Fireball"));
        assertTrue(notificationText.contains("Mana Cost: 3"));
        assertFalse(notificationText.contains("Attack:"));
        assertFalse(notificationText.contains("Health:"));
        assertTrue(notificationText.contains("Description: Deal 4 damage to a single target"));
    }

    @Test
    public void testNullCard() {
        DummyTell testTell = new DummyTell();
        altTell = testTell;

        showEnlargedCardInfo(null, null);

        assertEquals("Invalid card! No information to display", testTell.getLastNotificationText());
        assertNull(testTell.getLastDrawCardNode());
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
        invalidCreature.setAttack(0); 

        showEnlargedCardInfo(null, invalidCreature);

        String notificationText = testTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertTrue(notificationText.contains("Attack: 0")); 
        assertTrue(notificationText.contains("Health: 2"));
        assertTrue(notificationText.contains("Name: Broken Wolf"));
    }

    public static class DummyTell {
        private String lastNotificationText;
        private ObjectNode lastDrawCardNode;

        public void tell(ObjectNode message) {
            if (message == null) return;
            String messageType = message.get("messagetype").asText();
            if ("addPlayer1Notification".equals(messageType)) {
                this.lastNotificationText = message.get("text").asText();
            } else if ("drawCard".equals(messageType)) {
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