import com.fasterxml.jackson.databind.node.ObjectNode;
import events.EndTurnClicked;
import events.Heartbeat;
import events.UnitMoving;
import events.UnitStopped;
import org.junit.Test;
import play.libs.Json;
import structures.GameState;

import static org.junit.Assert.*;

public class TurnSwitchTest {

    private ObjectNode msg(String type) {
        ObjectNode n = Json.newObject();
        n.put("messagetype", type);
        return n;
    }

    @Test
    public void testEndTurnRequestSetsPendingState() {
        GameState gs = new GameState();

        EndTurnClicked endTurn = new EndTurnClicked();
        endTurn.processEvent(null, gs, msg("endturnclicked"));

        assertTrue(gs.isEndTurnRequested());
        assertEquals(GameState.TurnPhase.END_TURN_PENDING, gs.phase);
    }




    @Test
    public void testHeartbeatSwitchesTurnWhenReady() {
        GameState gs = new GameState();

        gs.isPlayer1Turn = true;
        gs.requestEndTurn();

        int roundsBefore = gs.getRounds();

        Heartbeat hb = new Heartbeat();
        hb.processEvent(null, gs, msg("heartbeat"));

        assertFalse(gs.isPlayer1Turn);
        assertFalse(gs.isEndTurnRequested());
        assertEquals(GameState.TurnPhase.AI_TURN, gs.phase);
        assertTrue(gs.getRounds() >= roundsBefore);
    }

    @Test
    public void testFullRoundIncrementRule() {
        GameState gs = new GameState();
        gs.isPlayer1Turn = true;

        Heartbeat hb = new Heartbeat();
        int roundsStart = gs.getRounds();

        // P1 -> P2 (should NOT increment rounds)
        gs.requestEndTurn();
        hb.processEvent(null, gs, msg("heartbeat"));
        int roundsAfterP2 = gs.getRounds();

        // P2 -> P1 (should increment rounds by 1)
        gs.requestEndTurn();
        hb.processEvent(null, gs, msg("heartbeat"));
        int roundsAfterP1 = gs.getRounds();

        assertEquals(roundsStart, roundsAfterP2);
        assertEquals(roundsStart + 1, roundsAfterP1);
    }
}