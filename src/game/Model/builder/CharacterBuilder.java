package game.Model.builder;

import game.Model.characters.AbstractCharacter;
import game.Util.GameLogger;

/**
 * Abstract builder for creating characters with customizable attributes.
 * Implements the Builder pattern to allow flexible character creation.
 *
 * Attributes can be modified within constraints:
 * - Each attribute can be increased by up to 3 points
 * - Each attribute can be decreased by up to 2 points
 * - Total points must remain the same as the default
 */
public abstract class CharacterBuilder<T extends AbstractCharacter> {

    // Default values
    private static final int DEFAULT_HEALTH = 100;
    private static final int DEFAULT_POWER = 10; // Middle value of 4-14 range
    private static final double DEFAULT_EVASION = 0.25;

    // Current values
    private int health = DEFAULT_HEALTH;
    private int power = DEFAULT_POWER;
    private double evasionChance = DEFAULT_EVASION;

    // Track modifications for validation
    private int healthModification = 0;
    private int powerModification = 0;
    private int evasionModification = 0; // Evasion modifications in percentage points

    /**
     * Sets the health value with validation.
     *
     * @param health The desired health value
     * @return This builder for chaining
     */
    public CharacterBuilder<T> withHealth(int health) {
        int modification = health - DEFAULT_HEALTH;

        if (modification > 3 || modification < -2) {
            GameLogger.getInstance().log("Invalid health modification: " + modification + ". Must be between -2 and +3");
            return this;
        }

        this.health = health;
        this.healthModification = modification;
        return this;
    }

    /**
     * Sets the power value with validation.
     *
     * @param power The desired power value
     * @return This builder for chaining
     */
    public CharacterBuilder<T> withPower(int power) {
        int modification = power - DEFAULT_POWER;

        if (modification > 3 || modification < -2) {
            GameLogger.getInstance().log("Invalid power modification: " + modification + ". Must be between -2 and +3");
            return this;
        }

        this.power = power;
        this.powerModification = modification;
        return this;
    }

    /**
     * Sets the evasion chance with validation.
     * Evasion is modified in percentage points (e.g., 0.25 -> 0.28 is +3 points)
     *
     * @param evasionChance The desired evasion chance (0.0 to 1.0)
     * @return This builder for chaining
     */
    public CharacterBuilder<T> withEvasionChance(double evasionChance) {
        int modification = (int)((evasionChance - DEFAULT_EVASION) * 100);

        if (modification > 3 || modification < -2) {
            GameLogger.getInstance().log("Invalid evasion modification: " + modification + "%. Must be between -2% and +3%");
            return this;
        }

        this.evasionChance = evasionChance;
        this.evasionModification = modification;
        return this;
    }

    /**
     * Validates that total point modifications sum to zero.
     *
     * @return true if valid, false otherwise
     */
    private boolean validatePointBalance() {
        int totalModification = healthModification + powerModification + evasionModification;

        if (totalModification != 0) {
            GameLogger.getInstance().log("Invalid character build: Total point modifications must sum to 0, but got " + totalModification);
            return false;
        }

        return true;
    }

    /**
     * Gets the current health value.
     *
     * @return The health value
     */
    protected int getHealth() {
        return health;
    }

    /**
     * Gets the current power value.
     *
     * @return The power value
     */
    protected int getPower() {
        return power;
    }

    /**
     * Gets the current evasion chance.
     *
     * @return The evasion chance
     */
    protected double getEvasionChance() {
        return evasionChance;
    }

    /**
     * Validates the point balance for subclasses.
     *
     * @return true if valid, false otherwise
     */
    protected boolean isValidBuild() {
        return validatePointBalance();
    }

    /**
     * Resets the builder to default values.
     *
     * @return This builder for chaining
     */
    public CharacterBuilder<T> reset() {
        this.health = DEFAULT_HEALTH;
        this.power = DEFAULT_POWER;
        this.evasionChance = DEFAULT_EVASION;
        this.healthModification = 0;
        this.powerModification = 0;
        this.evasionModification = 0;
        return this;
    }

    /**
     * Builds the character with current settings.
     *
     * @return The built character, or null if validation fails
     */
    public abstract T build();

    /**
     * Applies the builder settings to a character.
     *
     * @param character The character to configure
     */
    protected void applySettings(AbstractCharacter character) {
        character.setHealth(health);
        character.setPower(power);
    }
}