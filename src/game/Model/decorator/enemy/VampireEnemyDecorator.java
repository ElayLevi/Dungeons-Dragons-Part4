package game.Model.decorator.enemy;

import game.Model.characters.Enemy;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that heals the enemy when attacking.
 * Steals 10% of damage dealt as health.
 */
public class VampireEnemyDecorator extends CharacterDecorator {

    private static final double LIFE_STEAL_PERCENT = 0.1;

    public VampireEnemyDecorator(Enemy enemy, int durationSeconds) {
        super(enemy, durationSeconds);
        GameLogger.getInstance().log(enemy.enemyDiscription() + " gained vampiric powers!");
    }

    @Override
    public void attack(Combatant target) {
        if (!isActive()) {
            super.attack(target);
            return;
        }

        int targetHealthBefore = target.getHealth();
        super.attack(target);
        int targetHealthAfter = target.getHealth();

        // Calculate damage dealt and heal based on it
        int damageDealt = targetHealthBefore - targetHealthAfter;
        if (damageDealt > 0) {
            int healAmount = (int)(damageDealt * LIFE_STEAL_PERCENT);
            heal(healAmount);
            GameLogger.getInstance().log("Vampire healed " + healAmount + " HP from attack!");
        }
    }

    @Override
    public String getDecoratorName() {
        return "Vampire";
    }
}
