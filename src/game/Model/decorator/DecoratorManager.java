package game.Model.decorator;

import game.Model.characters.*;
import game.Model.decorator.player.*;
import game.Model.decorator.enemy.*;
import game.Model.engine.GameWorld;
import game.Util.GameLogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the application and removal of decorators on characters.
 * Handles the lifecycle of temporary effects.
 *
 * @author [Your Name]
 * @author [Partner Name if applicable]
 * @id [Your ID]
 * @id [Partner ID if applicable]
 */
public class DecoratorManager {

    private static DecoratorManager instance;

    // Track decorated characters and their original forms
    private final Map<AbstractCharacter, AbstractCharacter> originalCharacters = new ConcurrentHashMap<>();
    private final Map<AbstractCharacter, List<CharacterDecorator>> activeDecorators = new ConcurrentHashMap<>();

    // Available player decorators
    public enum PlayerDecoratorType {
        BOOSTED_ATTACK("Boosted Attack", BoostedAttackDecorator.class),
        SHIELDED("Shield", ShieldedPlayerDecorator.class),
        SPEED_BOOST("Speed Boost", SpeedBoostDecorator.class),
        REGENERATION("Regeneration", RegenerationDecorator.class),
        MAGIC_AMPLIFIER("Magic Amplifier", MagicAmplifierDecorator.class);

        private final String displayName;
        private final Class<? extends CharacterDecorator> decoratorClass;

        PlayerDecoratorType(String displayName, Class<? extends CharacterDecorator> decoratorClass) {
            this.displayName = displayName;
            this.decoratorClass = decoratorClass;
        }

        public String getDisplayName() { return displayName; }
    }

    // Available enemy decorators
    public enum EnemyDecoratorType {
        EXPLODING("Exploding", ExplodingEnemyDecorator.class),
        CAMOUFLAGED("Camouflaged", CamouflagedEnemyDecorator.class),
        ENRAGED("Enraged", EnragedEnemyDecorator.class),
        VAMPIRE("Vampire", VampireEnemyDecorator.class),
        TELEPORTING("Teleporting", TeleportingEnemyDecorator.class);

        private final String displayName;
        private final Class<? extends CharacterDecorator> decoratorClass;

        EnemyDecoratorType(String displayName, Class<? extends CharacterDecorator> decoratorClass) {
            this.displayName = displayName;
            this.decoratorClass = decoratorClass;
        }
    }

    private DecoratorManager() {
        // Start cleanup thread
        startCleanupThread();
    }

    public static DecoratorManager getInstance() {
        if (instance == null) {
            instance = new DecoratorManager();
        }
        return instance;
    }

    /**
     * Applies a decorator to a player character.
     * Note: The original player remains in the game list, decorator is tracked separately.
     *
     * @param player The player to decorate
     * @param type The type of decorator to apply
     * @param durationSeconds Duration of the effect
     * @return The original player (for chaining)
     */
    public PlayerCharacter applyPlayerDecorator(PlayerCharacter player, PlayerDecoratorType type, int durationSeconds) {
        try {
            // Get the base character if already decorated
            AbstractCharacter base = getBaseCharacter(player);
            if (!(base instanceof PlayerCharacter)) {
                GameLogger.getInstance().log("Error: Base character is not a player");
                return player;
            }

            // Create the decorator
            CharacterDecorator decorator = type.decoratorClass
                    .getConstructor(PlayerCharacter.class, int.class)
                    .newInstance(player, durationSeconds);

            // Track the decoration
            activeDecorators.computeIfAbsent(player, k -> new ArrayList<>()).add(decorator);

            GameLogger.getInstance().log("Applied " + type.getDisplayName() + " to " + player.getName());

            // Return the original player - decorators are tracked internally
            return player;

        } catch (Exception e) {
            GameLogger.getInstance().log("Failed to apply decorator: " + e.getMessage());
            e.printStackTrace();
            return player;
        }
    }

    /**
     * Applies a random decorator to an enemy.
     * Note: The original enemy remains in the game list, decorator is tracked separately.
     *
     * @param enemy The enemy to decorate
     * @param durationSeconds Duration of the effect
     * @return The original enemy (for chaining)
     */
    public Enemy applyRandomEnemyDecorator(Enemy enemy, int durationSeconds) {
        Random rand = new Random();
        EnemyDecoratorType[] types = EnemyDecoratorType.values();
        EnemyDecoratorType selectedType = types[rand.nextInt(types.length)];

        try {
            // Get the base character if already decorated
            AbstractCharacter base = getBaseCharacter(enemy);
            if (!(base instanceof Enemy)) {
                GameLogger.getInstance().log("Error: Base character is not an enemy");
                return enemy;
            }

            // Create the decorator
            CharacterDecorator decorator = selectedType.decoratorClass
                    .getConstructor(Enemy.class, int.class)
                    .newInstance(enemy, durationSeconds);

            // Track the decoration
            activeDecorators.computeIfAbsent(enemy, k -> new ArrayList<>()).add(decorator);

            GameLogger.getInstance().log("Applied " + selectedType.displayName + " to " + enemy.enemyDiscription());

            // Return the original enemy - decorators are tracked internally
            return enemy;

        } catch (Exception e) {
            GameLogger.getInstance().log("Failed to apply enemy decorator: " + e.getMessage());
            e.printStackTrace();
            return enemy;
        }
    }

    /**
     * Gets the base character without decorators.
     */
    private AbstractCharacter getBaseCharacter(AbstractCharacter character) {
        if (character instanceof CharacterDecorator) {
            return ((CharacterDecorator) character).getBaseCharacter();
        }
        return character;
    }

    /**
     * Gets the effective power of a character including all active decorators.
     *
     * @param character The character to check
     * @return The modified power value
     */
    public int getEffectivePower(AbstractCharacter character) {
        int basePower = character.getPower();
        List<CharacterDecorator> decorators = activeDecorators.get(character);

        if (decorators == null || decorators.isEmpty()) {
            return basePower;
        }

        // Apply all active decorator effects
        double multiplier = 1.0;
        for (CharacterDecorator decorator : decorators) {
            if (decorator.isActive()) {
                // Check decorator type and apply multiplier
                String decoratorName = decorator.getDecoratorName();
                switch (decoratorName) {
                    case "BoostedAttack":
                        multiplier *= 1.5;
                        break;
                    case "MagicAmplifier":
                        if (character.getElement() != null) {
                            multiplier *= 1.4;
                        }
                        break;
                    case "Enraged":
                        // Calculate rage bonus based on health
                        double healthPercent = (double)character.getHealth() / 50.0;
                        double rageMultiplier = 1.0 + (0.5 * (1.0 - healthPercent));
                        multiplier *= rageMultiplier;
                        break;
                }
            }
        }

        return (int)(basePower * multiplier);
    }

    /**
     * Processes damage for a character considering decorators.
     *
     * @param character The character receiving damage
     * @param amount The base damage amount
     * @param source The source of damage
     * @return The actual damage to apply
     */
    public int processDamage(AbstractCharacter character, int amount, AbstractCharacter source) {
        List<CharacterDecorator> decorators = activeDecorators.get(character);

        if (decorators == null || decorators.isEmpty()) {
            return amount;
        }

        int modifiedDamage = amount;

        for (CharacterDecorator decorator : decorators) {
            if (decorator.isActive()) {
                String decoratorName = decorator.getDecoratorName();

                if ("Shield".equals(decoratorName) && decorator instanceof ShieldedPlayerDecorator) {
                    // Let the decorator handle the damage
                    decorator.receiveDamage(amount, source);
                    return 0; // Damage was handled by decorator
                }

                if ("Camouflaged".equals(decoratorName) && decorator instanceof CamouflagedEnemyDecorator) {
                    // Check if can be damaged
                    CamouflagedEnemyDecorator camo = (CamouflagedEnemyDecorator) decorator;
                    if (!camo.hasAttacked) {
                        GameLogger.getInstance().log("Attack missed - enemy is camouflaged!");
                        return 0;
                    }
                }
            }
        }

        return modifiedDamage;
    }

    /**
     * Removes all expired decorators from a character.
     */
    private void cleanupExpiredDecorators() {
        // Clean up expired decorators
        for (Map.Entry<AbstractCharacter, List<CharacterDecorator>> entry : new HashMap<>(activeDecorators).entrySet()) {
            AbstractCharacter character = entry.getKey();
            List<CharacterDecorator> decorators = entry.getValue();

            // Remove expired decorators
            decorators.removeIf(d -> !d.isActive());

            if (decorators.isEmpty()) {
                // All decorators expired
                activeDecorators.remove(character);

                String name = "";
                if (character instanceof PlayerCharacter) {
                    name = ((PlayerCharacter) character).getName();
                } else if (character instanceof Enemy) {
                    name = ((Enemy) character).enemyDiscription();
                }

                GameLogger.getInstance().log("All decorators expired on " + name);
            }
        }
    }

    /**
     * Starts a background thread to clean up expired decorators.
     */
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Check every second
                    cleanupExpiredDecorators();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "DecoratorCleanup");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    /**
     * Gets active decorators for a character.
     *
     * @param character The character to check
     * @return List of active decorators
     */
    public List<CharacterDecorator> getActiveDecorators(AbstractCharacter character) {
        AbstractCharacter base = getBaseCharacter(character);
        List<CharacterDecorator> decorators = activeDecorators.get(base);
        if (decorators == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(decorators);
    }

    /**
     * Checks if a character has a specific decorator type.
     *
     * @param character The character to check
     * @param decoratorClass The decorator class to check for
     * @return true if the character has this decorator active
     */
    public boolean hasDecorator(AbstractCharacter character, Class<? extends CharacterDecorator> decoratorClass) {
        return getActiveDecorators(character).stream()
                .anyMatch(d -> d.getClass().equals(decoratorClass) && d.isActive());
    }

    /**
     * Removes all decorators from a character.
     *
     * @param character The character to clear
     */
    public void removeAllDecorators(AbstractCharacter character) {
        AbstractCharacter base = getBaseCharacter(character);
        activeDecorators.remove(base);
        activeDecorators.remove(character);

        originalCharacters.remove(character);
        originalCharacters.remove(base);

        GameLogger.getInstance().log("Removed all decorators from character");
    }

    /**
     * Gets information about all active decorators in the game.
     *
     * @return Map of characters to their active decorators
     */
    public Map<String, List<String>> getActiveDecoratorInfo() {
        Map<String, List<String>> info = new HashMap<>();

        GameWorld world = GameWorld.getInstance();

        // Check players
        for (PlayerCharacter player : world.getPlayers()) {
            List<CharacterDecorator> decorators = getActiveDecorators(player);
            if (!decorators.isEmpty()) {
                List<String> decoratorInfo = new ArrayList<>();
                for (CharacterDecorator d : decorators) {
                    if (d.isActive()) {
                        decoratorInfo.add(d.getDecoratorName() + " (" + d.getRemainingSeconds() + "s)");
                    }
                }
                if (!decoratorInfo.isEmpty()) {
                    info.put(player.getName(), decoratorInfo);
                }
            }
        }

        // Check enemies
        for (Enemy enemy : world.getEnemies()) {
            List<CharacterDecorator> decorators = getActiveDecorators(enemy);
            if (!decorators.isEmpty()) {
                List<String> decoratorInfo = new ArrayList<>();
                for (CharacterDecorator d : decorators) {
                    if (d.isActive()) {
                        decoratorInfo.add(d.getDecoratorName() + " (" + d.getRemainingSeconds() + "s)");
                    }
                }
                if (!decoratorInfo.isEmpty()) {
                    info.put(enemy.enemyDiscription() + "@" + enemy.getPosition(), decoratorInfo);
                }
            }
        }

        return info;
    }
}