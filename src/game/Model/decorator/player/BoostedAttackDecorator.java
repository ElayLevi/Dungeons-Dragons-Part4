package game.Model.decorator.player;
import game.Model.characters.PlayerCharacter;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that boosts attack power for player characters.
 * Adds 50% more damage to all attacks.
 */
public class BoostedAttackDecorator extends CharacterDecorator {

    private static final double DAMAGE_MULTIPLIER = 1.5;

    public BoostedAttackDecorator(PlayerCharacter player, int durationSeconds) {
        super(player, durationSeconds);
        GameLogger.getInstance().log(player.getName() + " gained Boosted Attack!");
    }

    @Override
    public int getPower() {
        if (!isActive()) {
            return super.getPower();
        }
        // 50% power boost
        return (int)(super.getPower() * DAMAGE_MULTIPLIER);
    }

    @Override
    public void attack(Combatant target) {
        if (!isActive()) {
            super.attack(target);
            return;
        }

        GameLogger.getInstance().log("Boosted attack activated! Damage x" + DAMAGE_MULTIPLIER);
        super.attack(target);
    }

    @Override
    public String getDecoratorName() {
        return "BoostedAttack";
    }
}
