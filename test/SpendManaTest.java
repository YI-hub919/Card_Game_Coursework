import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerManaTest {
    private Player player;
    private Card fireballCard; // Spell Card: Fireball (3 Mana)
    private Card werewolfCard; // Creature Card: Werewolf (2 Mana)

    // Initialize test environment
    @BeforeEach
    public void init() {
        player = new Player(5); // Player initial mana: 5 (max mana 5)
        fireballCard = new SpellCard("Fireball", 3, "Deal 4 damage to a single target");
        werewolfCard = new CreatureCard("Werewolf", 2, 2, 2);
    }

    // Test Case 1: Verify correct reading of selected card's mana cost
    @Test
    public void testCheckCardManaCost() {
        assertEquals(3, fireballCard.getManaCost());
        assertEquals(2, werewolfCard.getManaCost());
    }

    // Test Case 2 + 3: When mana is sufficient, deduct mana and trigger effect
    @Test
    public void testUseCardWithEnoughMana() {
        // Initial mana 5 ≥ Werewolf's 2 mana
        boolean isSuccess = player.useCard(werewolfCard);
        assertTrue(isSuccess);
        assertEquals(5 - 2, player.getCurrentMana()); // Verify correct mana deduction

        // Remaining mana 3 ≥ Fireball's 3 mana
        isSuccess = player.useCard(fireballCard);
        assertTrue(isSuccess);
        assertEquals(3 - 3, player.getCurrentMana()); // Remaining mana: 0
    }

    // Test Case 4: When mana is insufficient, effect cannot be triggered and no mana deducted
    @Test
    public void testUseCardWithNotEnoughMana() {
        // Reset mana to 1 (< Werewolf's 2 mana)
        player.setCurrentMana(1);
        boolean isSuccess = player.useCard(werewolfCard);
        assertFalse(isSuccess);
        assertEquals(1, player.getCurrentMana()); // No mana deducted

        // Mana 0 < Fireball's 3 mana
        player.setCurrentMana(0);
        isSuccess = player.useCard(fireballCard);
        assertFalse(isSuccess);
        assertEquals(0, player.getCurrentMana());
    }

    // Boundary Test: Mana exactly equals card cost
    @Test
    public void testUseCardWithExactMana() {
        player.setCurrentMana(3); // Exactly equals Fireball's 3 mana
        boolean isSuccess = player.useCard(fireballCard);
        assertTrue(isSuccess);
        assertEquals(0, player.getCurrentMana());
    }
}