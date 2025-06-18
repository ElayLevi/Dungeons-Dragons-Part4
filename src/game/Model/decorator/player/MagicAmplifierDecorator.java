package game.Model.decorator.player;

import game.Model.characters.PlayerCharacter;
import game.Model.decorator.CharacterDecorator;
import game.Util.GameLogger;

/**
 * Decorator that amplifies magical attacks.
 * Increases magic damage by 40%.
 */
public class MagicAmplifierDecorator extends CharacterDecorator {

    private static final double MAGIC_MULTIPLIER = 1.4;

    public MagicAmplifierDecorator(PlayerCharacter player, int durationSeconds) {
        super(player, durationSeconds);
        GameLogger.getInstance().log(player.getName() + " gained Magic Amplifier!");
    }

    @Override
    public int getPower() {
        if (!isActive()) {
            return super.getPower();
        }

        // Check if the base character is a magic user (has an element)
        if (getElement() != null) {
            return (int)(super.getPower() * MAGIC_MULTIPLIER);
        }

        return super.getPower();
    }

    @Override
    public String getDecoratorName() {
        return "MagicAmplifier";
    }
}