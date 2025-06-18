package game.Model.decorator.enemy;

import game.Model.characters.Enemy;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Model.engine.GameWorld;
import game.Model.map.Position;
import game.Util.GameLogger;

import java.util.Random;

/**
 * Decorator that teleports enemy when health is low.
 * Teleports to random position when health drops below 30%.
 */
public class TeleportingEnemyDecorator extends CharacterDecorator {

    private static final double TELEPORT_HEALTH_THRESHOLD = 0.3; // 30%
    private boolean hasTeleported = false;

    public TeleportingEnemyDecorator(Enemy enemy, int durationSeconds) {
        super(enemy, durationSeconds);
        GameLogger.getInstance().log(enemy.enemyDiscription() + " gained teleportation ability!");
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        int healthBefore = getHealth();
        super.receiveDamage(amount, source);
        int healthAfter = getHealth();

        // Check if we crossed the threshold
        if (isActive() && !hasTeleported && healthAfter > 0) {
            double healthPercent = (double)healthAfter / 50.0; // 50 is enemy default max health

            if (healthPercent < TELEPORT_HEALTH_THRESHOLD) {
                teleportToRandomPosition();
                hasTeleported = true;
            }
        }
    }

    private void teleportToRandomPosition() {
        GameWorld world = GameWorld.getInstance();
        game.Model.map.GameMap map = world.getMap();
        Random rand = new Random();

        // Try to find a free position
        for (int attempts = 0; attempts < 50; attempts++) {
            int row = rand.nextInt(map.getNumRows());
            int col = rand.nextInt(map.getNumCols());
            Position newPos = new Position(row, col);

            if (map.isPositionFree(newPos)) {
                Position oldPos = getPosition();
                map.removeEntity(this);
                setPosition(newPos);
                map.addEntity(newPos, this);

                GameLogger.getInstance().log(((Enemy)getBaseCharacter()).enemyDiscription() +
                        " teleported from " + oldPos + " to " + newPos);
                break;
            }
        }
    }

    @Override
    public String getDecoratorName() {
        return "Teleporting";
    }
}