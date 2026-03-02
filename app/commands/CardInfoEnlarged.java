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

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

    private static DummyTell altTell = null; 

    // ==========Add undefined DummyTell interface (adapt to message sending logic) ==========
    public interface DummyTell {
        void tell(ObjectNode message); // Define tell method compatible with ActorRef.tell
    }

    // ==========Universal message sending method (resolve code redundancy) ==========
    /**
     * Unified message sending logic, compatible with DummyTell and ActorRef
     * @param out Original ActorRef
     * @param message JSON message to send
     */
    private static void sendJsonMessage(ActorRef out, ObjectNode message) {
        try {
            if (altTell != null) {
                altTell.tell(message); // Send via DummyTell
            } else {
                if (out != null) { // Add null protection for out
                    out.tell(message, ActorRef.noSender()); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========Universal exception handling method==========
    /**
     * Standardized exception handling: print stack trace + send notification to frontend
     * @param out ActorRef for sending notifications
     * @param errorMsg Error prompt text
     * @param e Caught exception
     */
    private static void handleException(ActorRef out, String errorMsg, Exception e) {
        e.printStackTrace(); // Print exception stack trace
        try {
            // Send error notification to frontend
            addPlayer1Notification(out, errorMsg + ": " + (e.getMessage() == null ? "Unknown error" : e.getMessage()), 5);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ======================== ORIGINAL METHODS========================
    public static void drawTile(ActorRef out, Tile tile, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawTile");
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            returnMessage.put("mode", mode);
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawUnit(ActorRef out, Unit unit, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawUnit");
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUnitAttack(ActorRef out, Unit unit, int attack) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitAttack");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.put("attack", attack);
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUnitHealth(ActorRef out, Unit unit, int health) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitHealth");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.put("health", health);
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile, boolean yfirst) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.put("yfirst", yfirst);
            // 修复：增加null校验
            returnMessage.set("unit", unit == null ? Json.newObject() : Json.toJson(unit));
            returnMessage.set("tile", tile == null ? Json.newObject() : Json.toJson(tile));
            sendJsonMessage(out, returnMessage); // Replace duplicate message sending logic
        } catch (Exception e) {
            e.printStackTrace();
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

            if (animation == null) return 0;
            return ((1000*(animation.getFrameStartEndIndices()[1]-animation.getFrameStartEndIndices()[0]))/animation.getFps())+50;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public static void setPlayer1Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Health");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPlayer2Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Health");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPlayer1Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Mana");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPlayer2Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Mana");
            returnMessage.set("player", player == null ? Json.newObject() : Json.toJson(player));
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawCard(ActorRef out, Card card, int position, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawCard");
            returnMessage.set("card", card == null ? Json.newObject() : Json.toJson(card));
            returnMessage.put("position", position);
            returnMessage.put("mode", mode);
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteCard(ActorRef out, int position) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "deleteCard");
            returnMessage.put("position", position);
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
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

            return ((1000*effect.getAnimationTextures().size())/effect.getFps())+50;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void addPlayer1Notification(ActorRef out, String text, int displayTimeSeconds) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "addPlayer1Notification");
            returnMessage.put("text", text == null ? "Unknown error" : text); // Null fallback
            returnMessage.put("seconds", displayTimeSeconds);
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Play projectile animation (optimized null check + unified message type)
     * @param out ActorRef for front-end communication
     * @param effect EffectAnimation instance for projectile
     * @param mode Animation mode (1=linear, 2=arc, 3=instant)
     * @param startTile Start position of projectile
     * @param targetTile Target position of projectile
     */
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
            returnMessage.put("mode", mode); 
            sendJsonMessage(out, returnMessage); 
        } catch (Exception e) {
            handleException(out, "Failed to play projectile animation", e); 
        }
    }

    // ========================Enlarged card display method (null protection) ========================
    /**
     * Core function: Get card info (mana/attack/health) + send enlarged display to UI
     * @param out ActorRef for front-end communication (template standard)
     * @param card Template's Card instance (structures.basic.Card)
     */
    public static void showEnlargedCardInfo(ActorRef out, Card card) {
        // 1. Validate input (null check)
        if (card == null) {
            addPlayer1Notification(out, "Invalid card! No information to display", 3);
            return;
        }

        // 2. Build enlarged card info (add null fallback for all properties)
        StringBuilder enlargedInfo = new StringBuilder();
        enlargedInfo.append("=== ENLARGED CARD INFO ===\n");
        // Null fallback for name
        enlargedInfo.append("Name: ").append(card.getName() == null ? "Unknown Card" : card.getName()).append("\n");
        // Fallback for mana cost (int type defaults to 0, no extra handling needed but semantics retained)
        enlargedInfo.append("Mana Cost: ").append(card.getManaCost()).append("\n");

        // 3. Add attack/health only for creature cards
        Card.CardType cardType = card.getType();
        if (cardType != null && cardType == Card.CardType.CREATURE) {
            enlargedInfo.append("Attack: ").append(card.getAttack()).append("\n");
            enlargedInfo.append("Health: ").append(card.getHealth()).append("\n");
        }

        // 4. Add description (null fallback)
        enlargedInfo.append("Description: ").append(card.getDescription() == null ? "No description" : card.getDescription());

        // 5. Send enlarged info to UI (via player notification + drawCard in enlarged mode)
        addPlayer1Notification(out, enlargedInfo.toString(), 5); // Show for 5 seconds
        drawCard(out, card, 0, 1); // Mode=1: Enlarged display (template's visual mode)
    }

    public static void setAltTell(DummyTell altTell) {
        CardInfoEnlarged.altTell = altTell;
    }
}