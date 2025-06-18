package game.Model.decorator.enemy;

import game.Model.characters.Enemy;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that increases damage as health decreases.
 * Up to 50% damage increase at low health.
 */
public class EnragedEnemyDecorator extends CharacterDecorator {

    private static final double MAX_RAGE_BONUS = 0.5; // 50% max bonus

    public EnragedEnemyDecorator(Enemy enemy, int durationSeconds) {
        super(enemy, durationSeconds);
        GameLogger.getInstance().log(enemy.enemyDiscription() + " became enraged!");
    }

    @Override
    public int getPower() {
        if (!isActive()) {
            return super.getPower();
        }

        // Calculate rage bonus based on missing health
        double healthPercent = (double)getHealth() / 50.0; // 50 is enemy default max health
        double rageMultiplier = 1.0 + (MAX_RAGE_BONUS * (1.0 - healthPercent));

        return (int)(super.getPower() * rageMultiplier);
    }

    @Override
    public void attack(Combatant target) {
        if (isActive() && getHealth() < 25) { // Less than half health
            GameLogger.getInstance().log("Enraged attack! Damage increased!");
        }
        super.attack(target);
    }

    @Override
    public String getDecoratorName() {
        return "Enraged";
    }
}