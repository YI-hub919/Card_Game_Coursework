package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that a unit instance has started a move. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitMoving”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Increase moving unit counter
		gameState.movingUnitsCount++;

		// In tests, message may not contain id
		if (message == null || message.get("id") == null) {
			return;
		}

		int unitid = message.get("id").asInt();
		// (If you later need unitid, use it here)
		
	}

}
