package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import akka.actor.ActorRef;
import play.libs.Json;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.EffectAnimation;
import structures.basic.UnitAnimation;
import structures.basic.UnitAnimationType;

/**
 * Integrates with template's Card class
 * Implements card info query + enlarged display
 * No duplicate class definitions, aligns with template logic
 * @author Adapted for game card display requirements
 */
@SuppressWarnings({"deprecation"})
public class CardInfoEnlarged {

    // Jackson ObjectMapper singleton (thread-safe for read-only operations)
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

    // Alternative message sender (for test/mock scenarios)
    private static DummyTell altTell = null; 

    // ========== DummyTell interface (adapt to message sending logic) ==========
    public interface DummyTell {
        void tell(ObjectNode message); // Define tell method compatible with ActorRef.tell
    }

    // ========== Universal message sending method (resolve code redundancy) ==========
    /**
     * Unified message sending logic, compatible with DummyTell and ActorRef
     * @param out Original ActorRef (nullable)
     * @param message JSON message to send (non-null)
     */
    private static void sendJsonMessage(ActorRef out, ObjectNode message) {
        if (message == null) {
            System.err.println("JSON message is null, skip sending");
            return;
        }
        
        try {
            if (altTell != null) {
                altTell.tell(message); // Send via DummyTell (test mode)
            } else if (out != null) { // Null protection for ActorRef
                out.tell(message, ActorRef.noSender()); 
            }
        } catch (Exception e) {
            System.err.println("Failed to send JSON message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== Universal exception handling method ==========
    /**
     * Standardized exception handling: print stack trace + send notification to frontend
     * @param out ActorRef for sending notifications (nullable)
     * @param errorMsg Error prompt text (non-null)
     * @param e Caught exception (non-null)
     */
    private static void handleException(ActorRef out, String errorMsg, Exception e) {
        if (errorMsg == null) errorMsg = "Unknown error";
        System.err.println(errorMsg + ": " + e.getMessage());
        e.printStackTrace();
        
        try {
            // Send user-friendly error notification to frontend
            addPlayer1Notification(out, errorMsg + ": " + (e.getMessage() == null ? "Unknown error" : e.getMessage()), 5);
        } catch (Exception ex) {
            System.err.println("Failed to send error notification: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ======================== ORIGINAL METHODS (optimized null checks) =========================
    public static void setUnitAttack(ActorRef out, Unit unit, int attack) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitAttack");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.put("attack", attack);
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set unit attack", e);
        }
    }

    public static void setUnitHealth(ActorRef out, Unit unit, int health) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitHealth");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.put("health", health);
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set unit health", e);
        }
    }

    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to move unit to tile", e);
        }
    }

    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile, boolean yfirst) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.put("yfirst", yfirst);
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to move unit to tile (yfirst mode)", e);
        }
    }

    public static int playUnitAnimation(ActorRef out, Unit unit, UnitAnimationType animationToPlay) {
        try {
            if (unit == null || animationToPlay == null) {
                addPlayer1Notification(out, "Invalid unit/animation type", 3);
                return 0;
            }
            
            unit.setAnimation(animationToPlay);
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "playUnitAnimation");
            returnMessage.set("unit", Json.toJson(unit));
            returnMessage.put("animation", animationToPlay.toString());
            sendJsonMessage(out, returnMessage); 

            UnitAnimation animation = switch (animationToPlay) {
                case idle -> unit.getAnimations().getIdle();
                case attack -> unit.getAnimations().getAttack();
                case channel -> unit.getAnimations().getChannel();
                case death -> unit.getAnimations().getDeath();
                case hit -> unit.getAnimations().getHit();
                case move -> unit.getAnimations().getMove();
                default -> null;
            };

            return (animation == null) ? 0 : ((1000 * (animation.getFrameStartEndIndices()[1] - animation.getFrameStartEndIndices()[0])) / animation.getFps()) + 50;
        } catch (Exception e) {
            handleException(out, "Failed to play unit animation", e);
            return 0;
        }
    }

    public static void deleteUnit(ActorRef out, Unit unit) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "deleteUnit");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to delete unit", e);
        }
    }

    public static void setPlayer1Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Health");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set player 1 health", e);
        }
    }

    public static void setPlayer2Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Health");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set player 2 health", e);
        }
    }

    public static void setPlayer1Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Mana");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set player 1 mana", e);
        }
    }

    public static void setPlayer2Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Mana");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to set player 2 mana", e);
        }
    }

    public static void deleteCard(ActorRef out, int position) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "deleteCard");
            returnMessage.put("position", position);
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            handleException(out, "Failed to delete card at position " + position, e);
        }
    }

    public static int playEffectAnimation(ActorRef out, EffectAnimation effect, Tile tile) {
        try {
            if (effect == null || tile == null) {
                addPlayer1Notification(out, "Invalid effect/tile for animation", 3);
                return 0;
            }
            
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "playEffectAnimation");
            returnMessage.set("effect", Json.toJson(effect));
            returnMessage.set("tile", Json.toJson(tile));
            sendJsonMessage(out, returnMessage); 

            return ((1000 * effect.getAnimationTextures().size()) / effect.getFps()) + 50;
        } catch (Exception e) {
            handleException(out, "Failed to play effect animation", e);
            return 0;
        }
    }

    public static void addPlayer1Notification(ActorRef out, String text, int displayTimeSeconds) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "addPlayer1Notification");
            returnMessage.put("text", text == null ? "Unknown error" : text); // Null fallback
            returnMessage.put("seconds", Math.max(1, displayTimeSeconds)); // Ensure valid display time
            sendJsonMessage(out, returnMessage);
        } catch (Exception e) {
            System.err.println("Failed to add player 1 notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void playProjectileAnimation(ActorRef out, EffectAnimation effect, int mode, Tile startTile, Tile targetTile) {
        try {
            if (effect == null || startTile == null || targetTile == null) {
                addPlayer1Notification(out, "Invalid projectile animation parameters", 3);
                return;
            }
            
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "playProjectileAnimation"); 
            returnMessage.set("effect", Json.toJson(effect));
            returnMessage.set("tile", Json.toJson(startTile));
            returnMessage.set("targetTile", Json.toJson(targetTile));
            returnMessage.put("mode", Math.max(1, Math.min(3, mode))); // Restrict mode to 1-3
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            handleException(out, "Failed to play projectile animation", e); 
        }
    }

    // ======================== Core Feature: Enlarged Card Display (Fixed) ========================
    /**
     * Core function: Get card info (mana/attack/health) + send enlarged display to UI
     * @param out ActorRef for front-end communication (template standard, nullable)
     * @param card Template's Card instance (structures.basic.Card, nullable)
     */
    public static void showEnlargedCardInfo(ActorRef out, Card card) {
        // Step 1: Validate input (null check with user notification)
        if (card == null) {
            addPlayer1Notification(out, "Invalid card! No information to display", 3);
            return;
        }

        try {
            // Step 2: Build JSON message for enlarged card display (frontend-compatible)
            ObjectNode enlargedCardMsg = Json.newObject();
            enlargedCardMsg.put("messagetype", "showEnlargedCardInfo"); // Unique message type for UI

            // Step 3: Populate card basic info (with null fallback)
            enlargedCardMsg.put("cardName", card.getName() == null ? "Unknown Card" : card.getName());
            enlargedCardMsg.put("manaCost", card.getManaCost()); // int type: default 0 if uninitialized
            enlargedCardMsg.put("description", card.getDescription() == null ? "No description" : card.getDescription());

            // Step 4: Add creature-only attributes (attack/health) with safe check
            Card.CardType cardType = card.getType();
            if (cardType != null && cardType == Card.CardType.CREATURE) {
                // Add attack/health only for creature cards (default 0 if uninitialized)
                enlargedCardMsg.put("attack", card.getAttack());
                enlargedCardMsg.put("health", card.getHealth());
            } else {
                // Set to -1 to indicate non-creature card (UI can hide these fields)
                enlargedCardMsg.put("attack", -1);
                enlargedCardMsg.put("health", -1);
            }

            // Step 5: Send the message to frontend (core fix: missing send logic)
            sendJsonMessage(out, enlargedCardMsg);

            // Optional: Send user notification for success
            addPlayer1Notification(out, "Showing enlarged info for: " + card.getName(), 2);

        } catch (Exception e) {
            // Unified exception handling (core fix: missing error handling)
            handleException(out, "Failed to show enlarged card info for " + card.getName(), e);
        }
    }

    // ======================== Setter for alternative tell (test/mock) ========================
    public static void setAltTell(DummyTell altTell) {
        CardInfoEnlarged.altTell = altTell;
    }
}