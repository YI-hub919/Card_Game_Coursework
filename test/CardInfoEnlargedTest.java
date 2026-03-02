package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import akka.actor.ActorRef;
import play.libs.Json;
import demo.CommandDemo;
import structures.basic.Card;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Fully compliant CardInfoEnlargedTest:
 * 1. Aligns with fixed CardInfoEnlarged logic
 * 2. Fixes ActorRef tell parameter error
 * 3. Adapts to JSON message structure
 * 4. Adds mock for CommandDemo.drawCard
 */
public class CardInfoEnlargedTest {

    // Reuse ObjectMapper from core class (consistent configuration)
    private static final ObjectMapper mapper = new ObjectMapper();
    // Mock tell for front-end message capture
    public static CardInfoEnlarged.DummyTell altTell = null;

    // ======================== Mock CommandDemo (avoid compilation error) ========================
    // Simulate CommandDemo.drawCard method (consistent with template logic)
    public static class CommandDemo {
        public static void drawCard(ActorRef out, Card card, int position, int mode) {
            try {
                ObjectNode returnMessage = Json.newObject();
                returnMessage.put("messagetype", "drawCard");
                returnMessage.set("card", card == null ? Json.newObject() : Json.toJson(card));
                returnMessage.put("position", position);
                returnMessage.put("mode", mode);
                
                // Use unified message sending logic
                if (altTell != null) altTell.tell(returnMessage);
                else if (out != null) {
                    out.tell(returnMessage, ActorRef.noSender()); // Fix: use noSender() instead of out
                }
            } catch (Exception e) {
                System.err.println("Mock drawCard failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ======================== Adapted notification method (align with core class) ========================
    public static void addPlayer1Notification(ActorRef out, String text, int displayTimeSeconds) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "addPlayer1Notification");
            returnMessage.put("text", text == null ? "Unknown error" : text); // Null fallback
            returnMessage.put("seconds", Math.max(1, displayTimeSeconds)); // Valid time check
            
            // Unified message sending (fix ActorRef tell parameter)
            if (altTell != null) {
                altTell.tell(returnMessage);
            } else if (out != null) {
                out.tell(returnMessage, ActorRef.noSender()); // Correct: noSender() instead of out
            }
        } catch (Exception e) {
            System.err.println("Failed to send player1 notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ======================== Core method (align with fixed showEnlargedCardInfo) ========================
    public static void showEnlargedCardInfo(ActorRef out, Card card) {
        // Step 1: Null check with notification
        if (card == null) {
            addPlayer1Notification(out, "Invalid card! No information to display", 3);
            return;
        }

        try {
            // Step 2: Build JSON message (align with core class's JSON structure)
            ObjectNode enlargedCardMsg = Json.newObject();
            enlargedCardMsg.put("messagetype", "showEnlargedCardInfo");
            enlargedCardMsg.put("cardName", card.getName() != null ? card.getName() : "N/A");
            enlargedCardMsg.put("manaCost", card.getManaCost());
            enlargedCardMsg.put("description", card.getDescription() != null ? card.getDescription() : "No description");

            // Step 3: Add creature-only attributes
            if (card.getType() == Card.CardType.CREATURE) {
                enlargedCardMsg.put("attack", card.getAttack());
                enlargedCardMsg.put("health", card.getHealth());
            } else {
                enlargedCardMsg.put("attack", -1); // Non-creature marker
                enlargedCardMsg.put("health", -1);
            }

            // Step 4: Send enlarged info message
            if (altTell != null) altTell.tell(enlargedCardMsg);
            else if (out != null) out.tell(enlargedCardMsg, ActorRef.noSender());

            // Step 5: Send success notification (string format for user readability)
            StringBuilder notificationText = new StringBuilder();
            notificationText.append("=== ENLARGED CARD INFO ===\n");
            notificationText.append("Name: ").append(card.getName() != null ? card.getName() : "N/A").append("\n");
            notificationText.append("Mana Cost: ").append(card.getManaCost()).append("\n");
            
            if (card.getType() == Card.CardType.CREATURE) {
                notificationText.append("Attack: ").append(card.getAttack()).append("\n");
                notificationText.append("Health: ").append(card.getHealth()).append("\n");
            }
            
            notificationText.append("Description: ").append(card.getDescription() != null ? card.getDescription() : "No description");
            addPlayer1Notification(out, notificationText.toString(), 5);

            // Step 6: Call drawCard (template logic)
            CommandDemo.drawCard(out, card, 0, 1);

        } catch (Exception e) {
            addPlayer1Notification(out, "Failed to show enlarged card info: " + e.getMessage(), 5);
            e.printStackTrace();
        }
    }

    // ======================== Test lifecycle ========================
    @AfterEach
    public void tearDown() {
        altTell = null; // Reset mock tell after each test
    }

    // ======================== Test cases (adapted to fixed logic) ========================
    @Test
    public void testCreatureCardEnlargedDisplay() {
        // 1. Initialize mock tell and test card
        altTell = new CardInfoEnlarged.DummyTell();
        Card creatureCard = new Card();
        creatureCard.setCardId("CRE001");
        creatureCard.setName("Werewolf");
        creatureCard.setType(Card.CardType.CREATURE);
        creatureCard.setManaCost(2);
        creatureCard.setAttack(2);
        creatureCard.setHealth(2);
        creatureCard.setDescription("Fierce wolf with sharp claws");

        // 2. Execute test method
        showEnlargedCardInfo(null, creatureCard);

        // 3. Assert card attribute correctness
        assertEquals("Werewolf", creatureCard.getName());
        assertEquals(2, creatureCard.getManaCost());
        assertEquals(2, creatureCard.getAttack());
        assertEquals(2, creatureCard.getHealth());
        assertEquals(Card.CardType.CREATURE, creatureCard.getType());

        // 4. Assert notification text (user-readable format)
        String expectedNotification = "=== ENLARGED CARD INFO ===\nName: Werewolf\nMana Cost: 2\nAttack: 2\nHealth: 2\nDescription: Fierce wolf with sharp claws";
        String actualNotification = altTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertEquals(expectedNotification, actualNotification);

        // 5. Assert JSON message for enlarged display (core logic check)
        ObjectNode enlargedMsg = altTell.getLastEnlargedCardMsg();
        assertNotNull(enlargedMsg, "Enlarged card JSON message should not be null");
        assertEquals("Werewolf", enlargedMsg.get("cardName").asText());
        assertEquals(2, enlargedMsg.get("manaCost").asInt());
        assertEquals(2, enlargedMsg.get("attack").asInt());
        assertEquals(2, enlargedMsg.get("health").asInt());
        assertEquals("Fierce wolf with sharp claws", enlargedMsg.get("description").asText());

        // 6. Assert drawCard message
        ObjectNode drawCardMsg = altTell.getLastDrawCardNode();
        assertNotNull(drawCardMsg, "Draw card JSON node should not be null");
        ObjectNode cardNode = (ObjectNode) drawCardMsg.get("card");
        assertEquals("Werewolf", cardNode.get("name").asText());
        assertEquals(0, drawCardMsg.get("position").asInt());
        assertEquals(1, drawCardMsg.get("mode").asInt());
    }

    @Test
    public void testSpellCardEnlargedDisplay() {
        // 1. Initialize mock tell and test card
        altTell = new CardInfoEnlarged.DummyTell();
        Card spellCard = new Card();
        spellCard.setCardId("SPE001");
        spellCard.setName("Fireball");
        spellCard.setType(Card.CardType.SPELL);
        spellCard.setManaCost(3);
        spellCard.setDescription("Deal 4 damage to a single target");

        // 2. Execute test method
        showEnlargedCardInfo(null, spellCard);

        // 3. Assert card attribute correctness
        assertEquals("Fireball", spellCard.getName());
        assertEquals(3, spellCard.getManaCost());
        assertEquals(0, spellCard.getAttack()); // Default for spell card
        assertEquals(0, spellCard.getHealth());
        assertEquals(Card.CardType.SPELL, spellCard.getType());

        // 4. Assert notification text (no attack/health for spell)
        String notificationText = altTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertTrue(notificationText.contains("Name: Fireball"));
        assertTrue(notificationText.contains("Mana Cost: 3"));
        assertFalse(notificationText.contains("Attack:"));
        assertFalse(notificationText.contains("Health:"));
        assertTrue(notificationText.contains("Description: Deal 4 damage to a single target"));

        // 5. Assert JSON message (attack/health = -1 for spell)
        ObjectNode enlargedMsg = altTell.getLastEnlargedCardMsg();
        assertEquals(-1, enlargedMsg.get("attack").asInt());
        assertEquals(-1, enlargedMsg.get("health").asInt());
    }

    @Test
    public void testNullCard() {
        // 1. Initialize mock tell
        altTell = new CardInfoEnlarged.DummyTell();

        // 2. Execute test method with null card
        showEnlargedCardInfo(null, null);

        // 3. Assert error notification
        assertEquals("Invalid card! No information to display", altTell.getLastNotificationText());
        // 4. Assert no drawCard message
        assertNull(altTell.getLastDrawCardNode());
        // 5. Assert no enlarged JSON message
        assertNull(altTell.getLastEnlargedCardMsg());
    }

    @Test
    public void testCreatureCardWithMissingAttack() {
        // 1. Initialize mock tell and invalid creature card
        altTell = new CardInfoEnlarged.DummyTell();
        Card invalidCreature = new Card();
        invalidCreature.setName("Broken Wolf");
        invalidCreature.setType(Card.CardType.CREATURE);
        invalidCreature.setManaCost(2);
        invalidCreature.setHealth(2);
        invalidCreature.setAttack(0); // Missing attack (set to 0)

        // 2. Execute test method
        showEnlargedCardInfo(null, invalidCreature);

        // 3. Assert notification text (attack = 0)
        String notificationText = altTell.getLastNotificationText().replaceAll("\\r\\n", "\n");
        assertTrue(notificationText.contains("Attack: 0"));
        assertTrue(notificationText.contains("Health: 2"));
        assertTrue(notificationText.contains("Name: Broken Wolf"));

        // 4. Assert JSON message (attack = 0)
        ObjectNode enlargedMsg = altTell.getLastEnlargedCardMsg();
        assertEquals(0, enlargedMsg.get("attack").asInt());
        assertEquals(2, enlargedMsg.get("health").asInt());
    }

    // ======================== Enhanced DummyTell (capture all message types) ========================
    public static class DummyTell implements CardInfoEnlarged.DummyTell {
        private String lastNotificationText;
        private ObjectNode lastDrawCardNode;
        private ObjectNode lastEnlargedCardMsg; // Capture enlarged JSON message

        @Override
        public void tell(ObjectNode message) {
            if (message == null) return;
            String messageType = message.get("messagetype").asText();
            
            // Classify messages by type
            switch (messageType) {
                case "addPlayer1Notification":
                    this.lastNotificationText = message.get("text").asText();
                    break;
                case "drawCard":
                    this.lastDrawCardNode = message;
                    break;
                case "showEnlargedCardInfo":
                    this.lastEnlargedCardMsg = message;
                    break;
                default:
                    System.out.println("Unrecognized message type: " + messageType);
            }
            
            // Print message for debug
            System.out.println("Front-end Message [" + messageType + "]: " + message.toPrettyString());
        }

        // Getters for test assertion
        public String getLastNotificationText() { return lastNotificationText; }
        public ObjectNode getLastDrawCardNode() { return lastDrawCardNode; }
        public ObjectNode getLastEnlargedCardMsg() { return lastEnlargedCardMsg; }
    }
}