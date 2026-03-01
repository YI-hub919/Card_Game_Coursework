import org.junit.Test;
import structures.GameState;
import static org.junit.Assert.assertEquals;

public class MaxManaTest {
    @Test
    public void testMaxMana() {
        GameState gameState = new GameState();
        for (int round = 1; round <= 100; round++) {
            gameState.setRounds(round);
            int expectedMaxMana;
            if (round >= 1 && round <= 8) {
                expectedMaxMana = round + 1;
            } else {
                expectedMaxMana = 9;
            }
            int actualMaxMana = gameState.getManaCapacity();

            assertEquals("actualMaxMana is round+1 when round between 1-8 and actualMaxMana is 9 when round>9", expectedMaxMana, actualMaxMana);
        }

    }
}
