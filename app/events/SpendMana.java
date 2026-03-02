public interface CardEffect {
    boolean executeEffect();
}

public class CreatureCard extends Card implements CardEffect {
    private int attack;
    private int health;

    public CreatureCard(String name, int manaCost, int attack, int health) {
        super(name, manaCost);
        this.attack = attack;
        this.health = health;
    }

    @Override
    public String getCardInfo() {
        return String.format(
            "Creature Card: %s | Mana Cost: %d | Attack: %d | Health: %d",
            getName(), getManaCost(), attack, health
        );
    }

    @Override
    public boolean executeEffect() {
        System.out.printf("Successfully summoned creature [%s]! Attack: %d, Health: %d%n", getName(), attack, health);
        return true;
    }

    public int getAttack() { return attack; }
    public int getHealth() { return health; }
}

public class SpellCard extends Card implements CardEffect {
    private String effect;

    public SpellCard(String name, int manaCost, String effect) {
        super(name, manaCost);
        this.effect = effect;
    }

    @Override
    public String getCardInfo() {
        return String.format(
            "Spell Card: %s | Mana Cost: %d | Effect: %s",
            getName(), getManaCost(), effect
        );
    }

    // Spell card effect: Trigger spell (simulation)
    @Override
    public boolean executeEffect() {
        System.out.printf("Successfully cast spell [%s]! Effect: %s%n", getName(), effect);
        return true;
    }

    // Getter method
    public String getEffect() { return effect; }
}