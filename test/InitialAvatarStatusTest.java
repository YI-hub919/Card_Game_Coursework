import org.junit.Test;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import structures.GameState;

import static org.junit.Assert.*;


public class InitialAvatarStatusTest {


    @Test
    public void checkInitalized() {

        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
        BasicCommands.altTell = altTell;
        GameState gameState = new GameState();
        Initalize initProcessor = new Initalize();
        initProcessor.initAvatar(null, gameState);

        assertEquals("avatar1 health is 20", 20, gameState.player1Avatar.getHealth());
        assertEquals("avatar1 attack is 2", 2, gameState.player1Avatar.getAttack());
        assertEquals("avatar2 health is 20", 20, gameState.player2Avatar.getHealth());
        assertEquals("avatar2 attack is 2", 2, gameState.player2Avatar.getAttack());

        assertEquals("avatar1 X axis", 1, gameState.player1Avatar.getPosition().getTilex());
        assertEquals("avatar1 Y axis", 2, gameState.player1Avatar.getPosition().getTiley());
        assertEquals("avatar2 X axis", 7, gameState.player2Avatar.getPosition().getTilex());
        assertEquals("avatar2 Y axis", 2, gameState.player2Avatar.getPosition().getTiley());
    }
}
