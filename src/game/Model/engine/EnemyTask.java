package game.Model.engine;
import game.Model.characters.Enemy;
import game.Model.Factory.EnemyFactory;
import game.Model.decorator.DecoratorManager;
import game.Util.GameLogger;

import java.util.Random;


/**
 * Runnable task for enemy execution in the thread pool.
 * Handles enemy death and respawning using the Factory pattern.
 */
public class EnemyTask implements Runnable {
    private final Enemy enemy;
    private final GameWorld world;

    /**
     * Creates a new enemy task.
     *
     * @param enemy The enemy to run
     */
    public EnemyTask(Enemy enemy) {
        this.enemy = enemy;
        this.world = GameWorld.getInstance();
    }

    @Override
    public void run() {
        try {
            if (!world.isRunning() || enemy.isDead()) {
                return;
            }

            // Execute enemy action
            enemy.run();

            // Check if enemy died during its action
            if (enemy.isDead()) {
                handleEnemyDeath();
            }

        } catch (Exception ex) {
            GameLogger.getInstance().log("Error in enemy task: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles enemy death and potential respawn.
     * Creates a new enemy if the thread pool is not at maximum capacity.
     */
    private void handleEnemyDeath() {
        GameLogger.getInstance().log(enemy.enemyDiscription() + " at " + enemy.getPosition() + " has died");

        // Calculate current active enemies
        long activeEnemies = world.getEnemies().stream().filter(e -> !e.isDead()).count();

        // Calculate maximum allowed enemies (3% of board size, min 1, max 10)
        int boardSize = world.getMap().getNumRows() * world.getMap().getNumCols();
        int maxEnemies = Math.max(1, Math.min(10, (int)(boardSize * 0.03)));

        GameLogger.getInstance().log("Active enemies: " + activeEnemies + ", Max allowed: " + maxEnemies);

        // Create a new enemy if we're below the maximum
        if (activeEnemies < maxEnemies) {
            Enemy newEnemy = EnemyFactory.createEnemy();
            if (newEnemy != null) {
                // Schedule the new enemy
                world.scheduleNewEnemy(newEnemy);
                GameLogger.getInstance().log("Spawned new enemy to replace the dead one");
            }
        } else {
            GameLogger.getInstance().log("Maximum enemy count reached, not spawning replacement");
        }

        if (activeEnemies < maxEnemies) {
            Enemy newEnemy = EnemyFactory.createEnemy();
            if (newEnemy != null) {
                // 30% chance to spawn with a decorator
                Random rand = new Random();
                if (rand.nextDouble() < 0.3) {
                    DecoratorManager manager = DecoratorManager.getInstance();
                    newEnemy = manager.applyRandomEnemyDecorator(newEnemy, 45); // 45 seconds
                }

                // Schedule the new enemy
                world.scheduleNewEnemy(newEnemy);
                GameLogger.getInstance().log("Spawned new enemy to replace the dead one");
            }
        }

    }
}