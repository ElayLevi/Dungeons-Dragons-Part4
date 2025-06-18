package game.Model.decorator;

import game.Model.characters.AbstractCharacter;
import game.Model.combat.Combatant;
import game.Model.combat.MagicElement;
import game.Model.core.GameEntity;
import game.Model.map.Position;

/**
 * Base decorator class for adding temporary abilities to characters.
 * Implements the Decorator pattern by wrapping an AbstractCharacter.
 *
 */
public abstract class CharacterDecorator extends AbstractCharacter {

    private final AbstractCharacter wrappedCharacter;
    private final long startTime;
    private final long duration; // in milliseconds

    /**
     * Creates a decorator that wraps a character.
     *
     * @param character The character to decorate
     * @param durationSeconds The duration of the effect in seconds
     */
    protected CharacterDecorator(AbstractCharacter character, int durationSeconds) {
        super();
        this.wrappedCharacter = character;
        this.startTime = System.currentTimeMillis();
        this.duration = durationSeconds * 1000L;
    }

    /**
     * Checks if the decorator effect is still active.
     *
     * @return true if the effect is active, false if expired
     */
    public boolean isActive() {
        return (System.currentTimeMillis() - startTime) < duration;
    }

    /**
     * Gets the remaining duration in seconds.
     *
     * @return Remaining seconds, or 0 if expired
     */
    public int getRemainingSeconds() {
        long remaining = duration - (System.currentTimeMillis() - startTime);
        return Math.max(0, (int)(remaining / 1000));
    }

    /**
     * Gets the wrapped character (for chaining decorators).
     *
     * @return The wrapped character
     */
    protected AbstractCharacter getWrappedCharacter() {
        return wrappedCharacter;
    }

    // ========== Delegated Methods ==========
    // Most methods delegate to the wrapped character

    @Override
    public Position getPosition() {
        return wrappedCharacter.getPosition();
    }

    @Override
    public void setPosition(Position p) {
        wrappedCharacter.setPosition(p);
    }

    @Override
    public boolean getVisible() {
        return wrappedCharacter.getVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        wrappedCharacter.setVisible(visible);
    }

    @Override
    public int getHealth() {
        return wrappedCharacter.getHealth();
    }

    @Override
    public void setHealth(int health) {
        wrappedCharacter.setHealth(health);
    }

    @Override
    public int getPower() {
        // Default implementation - can be overridden by specific decorators
        return wrappedCharacter.getPower();
    }

    @Override
    public boolean setPower(int p) {
        return wrappedCharacter.setPower(p);
    }

    @Override
    public double getEvasionChance() {
        // Default implementation - can be overridden by specific decorators
        return wrappedCharacter.getEvasionChance();
    }

    @Override
    public void setEvasionChance(double evasionChance) {
        wrappedCharacter.setEvasionChance(evasionChance);
    }

    @Override
    public boolean tryEvade() {
        // Default implementation - can be overridden by specific decorators
        return wrappedCharacter.tryEvade();
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        // Default implementation - can be overridden by specific decorators
        wrappedCharacter.receiveDamage(amount, source);
    }

    @Override
    public boolean isDead() {
        return wrappedCharacter.isDead();
    }

    @Override
    public void heal(int amount) {
        wrappedCharacter.heal(amount);
    }

    @Override
    public void takeDamage(int dmg) {
        // Default implementation - can be overridden by specific decorators
        wrappedCharacter.takeDamage(dmg);
    }

    @Override
    public void attack(Combatant target) {
        // Default implementation - can be overridden by specific decorators
        wrappedCharacter.attack(target);
    }

    @Override
    public MagicElement getElement() {
        return wrappedCharacter.getElement();
    }

    @Override
    public String getDisplaySymbol() {
        // Add a visual indicator for decorated characters
        return wrappedCharacter.getDisplaySymbol() + "*";
    }

    /**
     * Gets the base character without any decorators.
     * Useful for checking the original type.
     *
     * @return The base character
     */
    public AbstractCharacter getBaseCharacter() {
        if (wrappedCharacter instanceof CharacterDecorator) {
            return ((CharacterDecorator) wrappedCharacter).getBaseCharacter();
        }
        return wrappedCharacter;
    }

    /**
     * Abstract method to get the decorator name.
     *
     * @return The name of this decorator
     */
    public abstract String getDecoratorName();

    @Override
    public String toString() {
        return getDecoratorName() + " (" + getRemainingSeconds() + "s) -> " + wrappedCharacter.toString();
    }

    @Override
    public String getName() {
        return wrappedCharacter.getName();
    }



}