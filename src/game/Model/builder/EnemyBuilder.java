package game.Model.builder;

import game.Model.characters.*;
import game.Model.engine.GameWorld;
import game.Util.GameLogger;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating enemies with randomly distributed attributes.
 *
 */
public class EnemyBuilder extends CharacterBuilder<Enemy> {

    private final Random random = new Random();
    private Class<? extends Enemy> enemyClass;
    private final GameWorld world = GameWorld.getInstance();

    // Enemy-specific default values
    private static final int ENEMY_DEFAULT_HEALTH = 50;
    private static final int ENEMY_DEFAULT_POWER = 10;
    private static final double DEFAULT_EVASION = 0.25;

    /**
     * Sets the enemy class to build.
     *
     * @param enemyClass The enemy class (Goblin, Orc, Dragon)
     * @return This builder for chaining
     */
    public EnemyBuilder withEnemyClass(Class<? extends Enemy> enemyClass) {
        this.enemyClass = enemyClass;
        return this;
    }

    /**
     * Convenience method to create a Goblin.
     *
     * @return This builder for chaining
     */
    public EnemyBuilder asGoblin() {
        this.enemyClass = Goblin.class;
        return this;
    }

    /**
     * Convenience method to create an Orc.
     *
     * @return This builder for chaining
     */
    public EnemyBuilder asOrc() {
        this.enemyClass = Orc.class;
        return this;
    }

    /**
     * Convenience method to create a Dragon.
     *
     * @return This builder for chaining
     */
    public EnemyBuilder asDragon() {
        this.enemyClass = Dragon.class;
        return this;
    }

    /**
     * Randomly distributes attribute points while maintaining balance.
     *
     * @return This builder for chaining
     */
    public EnemyBuilder withRandomAttributes() {
        reset(); // Start fresh

        // Create list of possible modifications
        List<Integer> possibleMods = new ArrayList<>();
        for (int i = -2; i <= 3; i++) {
            possibleMods.add(i);
        }

        // Randomly assign modifications to ensure sum is 0
        int healthMod = 0, powerMod = 0, evasionMod = 0;

        // First, assign two random modifications
        healthMod = possibleMods.get(random.nextInt(possibleMods.size()));
        powerMod = possibleMods.get(random.nextInt(possibleMods.size()));

        // Calculate the third to balance
        evasionMod = -(healthMod + powerMod);

        // Check if evasion mod is within valid range
        if (evasionMod < -2 || evasionMod > 3) {
            // If not valid, try a different distribution
            return distributeRandomlyWithBacktrack();
        }

        // Apply modifications
        withHealth(ENEMY_DEFAULT_HEALTH + healthMod);
        withPower(ENEMY_DEFAULT_POWER + powerMod);
        withEvasionChance(DEFAULT_EVASION + (evasionMod / 100.0));

        GameLogger.getInstance().log("Random enemy attributes: Health " +
                (healthMod >= 0 ? "+" : "") + healthMod +
                ", Power " + (powerMod >= 0 ? "+" : "") + powerMod +
                ", Evasion " + (evasionMod >= 0 ? "+" : "") + evasionMod + "%");

        return this;
    }

    /**
     * Alternative random distribution method with backtracking.
     */
    private EnemyBuilder distributeRandomlyWithBacktrack() {
        reset();

        // Try up to 10 times to find a valid distribution
        for (int attempt = 0; attempt < 10; attempt++) {
            int totalPoints = 0;
            int healthMod = random.nextInt(6) - 2; // -2 to +3
            totalPoints += healthMod;

            int powerMod = random.nextInt(6) - 2; // -2 to +3
            totalPoints += powerMod;

            int evasionMod = -totalPoints; // Balance the points

            // Check if all modifications are within valid range
            if (evasionMod >= -2 && evasionMod <= 3) {
                withHealth(ENEMY_DEFAULT_HEALTH + healthMod);
                withPower(ENEMY_DEFAULT_POWER + powerMod);
                withEvasionChance(DEFAULT_EVASION + (evasionMod / 100.0));

                return this;
            }
        }

        // Fallback to no modifications
        GameLogger.getInstance().log("Could not find valid random distribution, using defaults");
        return this;
    }

    @Override
    public Enemy build() {
        if (enemyClass == null) {
            GameLogger.getInstance().log("Cannot build enemy: Enemy class is required");
            return null;
        }

        // Validate point balance
        if (!isValidBuild()) {
            return null;
        }

        try {
            Enemy enemy;

            if (enemyClass == Goblin.class) {
                enemy = new Goblin(world);
            } else if (enemyClass == Orc.class) {
                enemy = new Orc(world);
            } else if (enemyClass == Dragon.class) {
                enemy = new Dragon(world);
            } else {
                GameLogger.getInstance().log("Unknown enemy class: " + enemyClass);
                return null;
            }

            // Apply custom attributes
            enemy.setHealth(getHealth());
            enemy.setPower(getPower());
            // Note: Enemy classes don't expose setEvasionChance
            // This would need to be added to the base classes

            GameLogger.getInstance().log("Built " + enemyClass.getSimpleName() +
                    " with Health=" + getHealth() +
                    ", Power=" + getPower() +
                    ", Evasion=" + String.format("%.2f", getEvasionChance()));

            return enemy;

        } catch (Exception e) {
            GameLogger.getInstance().log("Error building enemy: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}