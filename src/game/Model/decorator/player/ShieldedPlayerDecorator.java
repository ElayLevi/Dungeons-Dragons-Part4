package game.Model.decorator.player;


import game.Model.characters.PlayerCharacter;
import game.Model.combat.Combatant;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that provides damage reduction and blocks first hit.
 * Blocks first damage in each round and reduces all damage by 50%.
 */
public class ShieldedPlayerDecorator extends CharacterDecorator {

    private static final double DAMAGE_REDUCTION = 0.5;
    private boolean firstHitBlocked = false;
    private long lastResetTime;
    private static final long RESET_INTERVAL = 10000; // Reset first hit block every 10 seconds

    public ShieldedPlayerDecorator(PlayerCharacter player, int durationSeconds) {
        super(player, durationSeconds);
        this.lastResetTime = System.currentTimeMillis();
        GameLogger.getInstance().log(player.getName() + " gained Shield!");
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        if (!isActive()) {
            super.receiveDamage(amount, source);
            return;
        }

        // Reset first hit block every interval
        if (System.currentTimeMillis() - lastResetTime > RESET_INTERVAL) {
            firstHitBlocked = false;
            lastResetTime = System.currentTimeMillis();
        }

        // Block first hit completely
        if (!firstHitBlocked) {
            firstHitBlocked = true;
            GameLogger.getInstance().log("Shield blocked all damage!");
            return;
        }

        // Reduce subsequent damage by 50%
        int reducedDamage = (int)(amount * DAMAGE_REDUCTION);
        GameLogger.getInstance().log("Shield reduced damage from " + amount + " to " + reducedDamage);
        super.receiveDamage(reducedDamage, source);
    }

    @Override
    public void takeDamage(int dmg) {
        if (!isActive()) {
            super.takeDamage(dmg);
            return;
        }

        // For direct damage (like magic waves), always apply reduction
        int reducedDamage = (int)(dmg * DAMAGE_REDUCTION);
        super.takeDamage(reducedDamage);
    }

    @Override
    public String getDecoratorName() {
        return "Shield";
    }
}

