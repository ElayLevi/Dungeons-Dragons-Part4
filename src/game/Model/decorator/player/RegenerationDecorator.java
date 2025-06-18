package game.Model.decorator.player;


import game.Model.characters.PlayerCharacter;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that regenerates health over time.
 * Heals 2% of max health every few seconds.
 */
public class RegenerationDecorator extends CharacterDecorator {

    private static final double HEAL_PERCENTAGE = 0.02; // 2% of max health
    private static final long HEAL_INTERVAL = 3000; // Every 3 seconds
    private long lastHealTime;

    public RegenerationDecorator(PlayerCharacter player, int durationSeconds) {
        super(player, durationSeconds);
        this.lastHealTime = System.currentTimeMillis();
        GameLogger.getInstance().log(player.getName() + " gained Regeneration!");
    }

    @Override
    public int getHealth() {
        checkAndHeal();
        return super.getHealth();
    }

    private void checkAndHeal() {
        if (!isActive() || isDead()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHealTime >= HEAL_INTERVAL) {
            int healAmount = (int)(100 * HEAL_PERCENTAGE); // Assuming 100 is max health
            heal(healAmount);
            lastHealTime = currentTime;
            GameLogger.getInstance().log("Regeneration healed " + healAmount + " HP");
        }
    }

    @Override
    public String getDecoratorName() {
        return "Regeneration";
    }
}

