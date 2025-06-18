package game.Model.Factory;

import game.Model.characters.*;
import game.Model.engine.GameWorld;
import game.Model.map.Position;
import game.Util.GameLogger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Factory class for creating enemy instances using the Factory Method pattern.
 * Uses a Map<String, Supplier> approach to avoid conditional logic.
 */
public class EnemyFactory {

    // Map of enemy types to their suppliers
    private static final Map<String, Supplier<Enemy>> enemySuppliers = new HashMap<>();

    // Static initializer to populate the suppliers map
    static {
        GameWorld world = GameWorld.getInstance();

        enemySuppliers.put("Goblin", () -> new Goblin(world));
        enemySuppliers.put("Orc", () -> new Orc(world));
        enemySuppliers.put("Dragon", () -> new Dragon(world));
    }

    /**
     * Creates an enemy based on dynamic criteria.
     * Prioritizes enemy types that don't currently exist on the board.
     * If all types exist, creates a random enemy.
     *
     * @return A new Enemy instance
     */
    public static Enemy createEnemy() {
        GameWorld world = GameWorld.getInstance();

        // Get current enemy types on the board
        Set<String> currentEnemyTypes = world.getEnemies().stream().filter(e -> !e.isDead()).map(Enemy::enemyDiscription).collect(Collectors.toSet());

        // Find enemy types that don't exist on the board
        List<String> missingTypes = enemySuppliers.keySet().stream().filter(type -> !currentEnemyTypes.contains(type)).collect(Collectors.toList());

        String selectedType;

        if (!missingTypes.isEmpty()) {
            // Choose randomly from missing types
            Random rand = new Random();
            selectedType = missingTypes.get(rand.nextInt(missingTypes.size()));
            GameLogger.getInstance().log("Creating missing enemy type: " + selectedType);
        } else {
            // All types exist, choose randomly
            List<String> allTypes = new ArrayList<>(enemySuppliers.keySet());
            Random rand = new Random();
            selectedType = allTypes.get(rand.nextInt(allTypes.size()));
            GameLogger.getInstance().log("All enemy types exist, creating random: " + selectedType);
        }

        // Create the enemy using the supplier
        Enemy newEnemy = enemySuppliers.get(selectedType).get();

        // Find a random free position for the enemy
        Position pos = findRandomFreePosition();
        if (pos != null) {
            newEnemy.setPosition(pos);
            world.getMap().addEntity(pos, newEnemy);
            world.getEnemies().add(newEnemy);
            GameLogger.getInstance().log("Created " + selectedType + " at " + pos);
        }

        return newEnemy;
    }

    /**
     * Creates a specific type of enemy.
     *
     * @param enemyType The type of enemy to create
     * @return A new Enemy instance, or null if type doesn't exist
     */
    public static Enemy createEnemyOfType(String enemyType) {
        Supplier<Enemy> supplier = enemySuppliers.get(enemyType);
        if (supplier == null) {
            GameLogger.getInstance().log("Unknown enemy type: " + enemyType);
            return null;
        }

        Enemy enemy = supplier.get();

        // Find position and add to world
        Position pos = findRandomFreePosition();
        if (pos != null) {
            enemy.setPosition(pos);
            GameWorld world = GameWorld.getInstance();
            world.getMap().addEntity(pos, enemy);
            world.getEnemies().add(enemy);
            GameLogger.getInstance().log("Created " + enemyType + " at " + pos);
        }

        return enemy;
    }

    /**
     * Adds a new enemy type to the factory.
     * This allows for easy extension with new enemy types.
     *
     * @param typeName The name of the enemy type
     * @param supplier The supplier that creates instances of this enemy
     */
    public static void registerEnemyType(String typeName, Supplier<Enemy> supplier) {
        enemySuppliers.put(typeName, supplier);
        GameLogger.getInstance().log("Registered new enemy type: " + typeName);
    }

    /**
     * Gets all available enemy types.
     *
     * @return Set of enemy type names
     */
    public static Set<String> getAvailableEnemyTypes() {
        return new HashSet<>(enemySuppliers.keySet());
    }

    /**
     * Finds a random free position on the map.
     *
     * @return A free Position, or null if no position is available
     */
    private static Position findRandomFreePosition() {
        GameWorld world = GameWorld.getInstance();
        game.Model.map.GameMap map = world.getMap();
        Random rand = new Random();

        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int row = rand.nextInt(map.getNumRows());
            int col = rand.nextInt(map.getNumCols());
            Position pos = new Position(row, col);

            if (map.isPositionFree(pos)) {
                return pos;
            }
        }

        GameLogger.getInstance().log("Could not find free position for enemy after " + maxAttempts + " attempts");
        return null;
    }
    /**
     * Just builds you the right subclass of Enemy, but does NOT add it to the world.
     */
    public static Enemy instantiateByType(String typeName) {
        Supplier<Enemy> s = enemySuppliers.get(typeName);
        if (s == null) throw new IllegalArgumentException("Unknown enemy type: "+typeName);
        return s.get();
    }
}