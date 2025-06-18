package game.Model.decorator.player;


import game.Model.characters.PlayerCharacter;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that allows double actions per turn.
 * Implementation depends on your turn system.
 */
public class SpeedBoostDecorator extends CharacterDecorator {

    private boolean extraActionUsed = false;
    private long lastTurnTime = 0;
    private static final long TURN_DURATION = 5000; // 5 seconds per "turn"

    public SpeedBoostDecorator(PlayerCharacter player, int durationSeconds) {
        super(player, durationSeconds);
        GameLogger.getInstance().log(player.getName() + " gained Speed Boost!");
    }

    /**
     * Checks if an extra action is available.
     * Resets every turn (5 seconds).
     *
     * @return true if extra action can be used
     */
    public boolean hasExtraAction() {
        if (!isActive()) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTurnTime > TURN_DURATION) {
            extraActionUsed = false;
            lastTurnTime = currentTime;
        }

        return !extraActionUsed;
    }

    /**
     * Uses the extra action.
     */
    public void useExtraAction() {
        extraActionUsed = true;
        GameLogger.getInstance().log("Extra action used!");
    }

    @Override
    public String getDecoratorName() {
        return "SpeedBoost";
    }
}

