package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import akka.actor.ActorRef;
import play.libs.Json;
import structures.basic.Card; // Use template's Card class (no redefinition)
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.EffectAnimation;
import structures.basic.UnitAnimation;
import structures.basic.UnitAnimationType;

/**
 * Adapted BasicCommands: Integrates with template's Card class
 * Implements card info query + enlarged display (JDK 11 compatible)
 * No duplicate class definitions, aligns with template logic
 * @author Adapted for game card display requirements
 */
public class BasicCommands {

    private static ObjectMapper mapper = new ObjectMapper();
    public static DummyTell altTell = null;

    // ======================== Keep Template's Original Methods (Unmodified) ========================
    // (Retain all original methods: drawTile/drawUnit/setUnitAttack/setPlayer1Health/etc.)
    // [Note: Copy all original template methods here to avoid breaking existing game logic]

    @SuppressWarnings({"deprecation"})
    public static void drawTile(ActorRef out, Tile tile, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawTile");
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(tile)));
            returnMessage.put("mode", mode);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void drawUnit(ActorRef out, Unit unit, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawUnit");
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(tile)));
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setUnitAttack(ActorRef out, Unit unit, int attack) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitAttack");
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            returnMessage.put("attack", attack);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setUnitHealth(ActorRef out, Unit unit, int health) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setUnitHealth");
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            returnMessage.put("health", health);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(tile)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void moveUnitToTile(ActorRef out, Unit unit, Tile tile, boolean yfirst) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "moveUnitToTile");
            returnMessage.put("yfirst", yfirst);
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(tile)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static int playUnitAnimation(ActorRef out, Unit unit, UnitAnimationType animationToPlay) {
        try {
            unit.setAnimation(animationToPlay);
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "playUnitAnimation");
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            returnMessage.put("animation", animationToPlay.toString());
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);

            UnitAnimation animation = null;
            if (animationToPlay.equals(UnitAnimationType.idle)) animation = unit.getAnimations().getIdle();
            if (animationToPlay.equals(UnitAnimationType.attack)) animation = unit.getAnimations().getAttack();
            if (animationToPlay.equals(UnitAnimationType.channel)) animation = unit.getAnimations().getChannel();
            if (animationToPlay.equals(UnitAnimationType.death)) animation = unit.getAnimations().getDeath();
            if (animationToPlay.equals(UnitAnimationType.hit)) animation = unit.getAnimations().getHit();
            if (animationToPlay.equals(UnitAnimationType.move)) animation = unit.getAnimations().getMove();

            if (animation==null) return 0;
            return ((1000*(animation.getFrameStartEndIndices()[1]-animation.getFrameStartEndIndices()[0]))/animation.getFps())+50;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void deleteUnit(ActorRef out, Unit unit) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "deleteUnit");
            returnMessage.put("unit", mapper.readTree(mapper.writeValueAsString(unit)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setPlayer1Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Health");
            returnMessage.put("player", mapper.readTree(mapper.writeValueAsString(player)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setPlayer2Health(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Health");
            returnMessage.put("player", mapper.readTree(mapper.writeValueAsString(player)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setPlayer1Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer1Mana");
            returnMessage.put("player", mapper.readTree(mapper.writeValueAsString(player)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void setPlayer2Mana(ActorRef out, Player player) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "setPlayer2Mana");
            returnMessage.put("player", mapper.readTree(mapper.writeValueAsString(player)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void drawCard(ActorRef out, Card card, int position, int mode) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawCard");
            returnMessage.put("card", mapper.readTree(mapper.writeValueAsString(card)));
            returnMessage.put("position", position);
            returnMessage.put("mode", mode);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteCard(ActorRef out, int position) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "deleteCard");
            returnMessage.put("position", position);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static int playEffectAnimation(ActorRef out, EffectAnimation effect, Tile tile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "playEffectAnimation");
            returnMessage.put("effect", mapper.readTree(mapper.writeValueAsString(effect)));
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(tile)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);

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
            returnMessage.put("text", text);
            returnMessage.put("seconds", displayTimeSeconds);
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void playProjectileAnimation(ActorRef out, EffectAnimation effect, int mode, Tile startTile, Tile targetTile) {
        try {
            ObjectNode returnMessage = Json.newObject();
            returnMessage.put("messagetype", "drawProjectile");
            returnMessage.put("effect", mapper.readTree(mapper.writeValueAsString(effect)));
            returnMessage.put("tile", mapper.readTree(mapper.writeValueAsString(startTile)));
            returnMessage.put("targetTile", mapper.readTree(mapper.writeValueAsString(targetTile)));
            returnMessage.put("mode", mapper.readTree(mapper.writeValueAsString(mode)));
            if (altTell!=null) altTell.tell(returnMessage);
            else out.tell(returnMessage, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================== New: Card Info + Enlarged Display (Integrated with Template) ========================
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

        // 2. Build enlarged card info (meets functional requirements)
        StringBuilder enlargedInfo = new StringBuilder();
        enlargedInfo.append("=== ENLARGED CARD INFO ===\n");
        enlargedInfo.append("Name: ").append(card.getName()).append("\n");
        enlargedInfo.append("Mana Cost: ").append(card.getManaCost()).append("\n");

        // 3. Add attack/health only for creature cards
        if (card.getType() == Card.CardType.CREATURE) {
            enlargedInfo.append("Attack: ").append(card.getAttack()).append("\n");
            enlargedInfo.append("Health: ").append(card.getHealth()).append("\n");
        }

        // 4. Add description (spell/creature)
        enlargedInfo.append("Description: ").append(card.getDescription());

        // 5. Send enlarged info to UI (via player notification + drawCard in enlarged mode)
        addPlayer1Notification(out, enlargedInfo.toString(), 5); // Show for 5 seconds
        drawCard(out, card, 0, 1); // Mode=1: Enlarged display (template's visual mode)
    }
}