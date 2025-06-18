package game.Model.builder;

import game.Model.characters.*;
import game.Util.GameLogger;

/**
 * Concrete builder for creating player characters with customizable attributes.
 *
 */
public class PlayerCharacterBuilder extends CharacterBuilder<PlayerCharacter> {

    private String playerName;
    private Class<? extends PlayerCharacter> characterClass;

    /**
     * Sets the player's name.
     *
     * @param name The player's name
     * @return This builder for chaining
     */
    public PlayerCharacterBuilder withName(String name) {
        this.playerName = name;
        return this;
    }

    /**
     * Sets the character class (Warrior, Mage, or Archer).
     *
     * @param characterClass The class of character to create
     * @return This builder for chaining
     */
    public PlayerCharacterBuilder withClass(Class<? extends PlayerCharacter> characterClass) {
        this.characterClass = characterClass;
        return this;
    }

    /**
     * Convenience method to create a Warrior.
     *
     * @return This builder for chaining
     */
    public PlayerCharacterBuilder asWarrior() {
        this.characterClass = Warrior.class;
        return this;
    }

    /**
     * Convenience method to create a Mage.
     *
     * @return This builder for chaining
     */
    public PlayerCharacterBuilder asMage() {
        this.characterClass = Mage.class;
        return this;
    }

    /**
     * Convenience method to create an Archer.
     *
     * @return This builder for chaining
     */
    public PlayerCharacterBuilder asArcher() {
        this.characterClass = Archer.class;
        return this;
    }

    @Override
    public PlayerCharacterBuilder withHealth(int health) {
        super.withHealth(health);
        return this;
    }

    @Override
    public PlayerCharacterBuilder withPower(int power) {
        super.withPower(power);
        return this;
    }

    @Override
    public PlayerCharacterBuilder withEvasionChance(double evasionChance) {
        super.withEvasionChance(evasionChance);
        return this;
    }

    @Override
    public PlayerCharacterBuilder reset() {
        super.reset();
        this.playerName = null;
        this.characterClass = null;
        return this;
    }

    @Override
    public PlayerCharacter build() {
        // Validate required fields
        if (playerName == null || playerName.trim().isEmpty()) {
            GameLogger.getInstance().log("Cannot build player: Name is required");
            return null;
        }

        if (characterClass == null) {
            GameLogger.getInstance().log("Cannot build player: Character class is required");
            return null;
        }

        // Validate point balance
        if (!isValidBuild()) {
            return null;
        }

        try {
            // Create the character instance
            PlayerCharacter character;

            if (characterClass == Warrior.class) {
                character = new Warrior(playerName);
            } else if (characterClass == Mage.class) {
                character = new Mage(playerName);
            } else if (characterClass == Archer.class) {
                character = new Archer(playerName);
            } else {
                GameLogger.getInstance().log("Unknown character class: " + characterClass);
                return null;
            }

            // Apply custom attributes
            applySettings(character);

            GameLogger.getInstance().log("Built " + characterClass.getSimpleName() +
                    " named " + playerName +
                    " with Health=" + getHealth() +
                    ", Power=" + getPower() +
                    ", Evasion=" + String.format("%.2f", getEvasionChance()));

            return character;

        } catch (Exception e) {
            GameLogger.getInstance().log("Error building character: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}