package game.Model.decorator.enemy;
import game.Model.characters.Enemy;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that makes enemy harder to target.
 * Can only be attacked after it attacks first.
 */
public class CamouflagedEnemyDecorator extends CharacterDecorator {

    public boolean hasAttacked = false;

    public CamouflagedEnemyDecorator(Enemy enemy, int durationSeconds) {
        super(enemy, durationSeconds);
        GameLogger.getInstance().log(enemy.enemyDiscription() + " became camouflaged!");
    }

    @Override
    public void attack(Combatant target) {
        super.attack(target);
        hasAttacked = true;
        GameLogger.getInstance().log("Camouflage broken - enemy revealed!");
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        if (!isActive() || hasAttacked) {
            super.receiveDamage(amount, source);
            return;
        }

        // While camouflaged and hasn't attacked, cannot be damaged
        GameLogger.getInstance().log("Attack missed - enemy is camouflaged!");
    }

    @Override
    public String getDecoratorName() {
        return "Camouflaged";
    }
}