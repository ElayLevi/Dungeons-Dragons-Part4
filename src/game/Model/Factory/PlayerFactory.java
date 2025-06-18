package game.Model.Factory;

import game.Model.characters.PlayerCharacter;
import game.Model.characters.Warrior;
import game.Model.characters.Mage;
import game.Model.characters.Archer;

/**
 * Factory for instantiating PlayerCharacter subclasses by saved type–name and player name.
 */
public final class PlayerFactory {
    private PlayerFactory() {
        // no instances
    }

    /**
     * Recreates a PlayerCharacter of the given subclass, using the original player name.
     *
     * @param typeName The simple class name of the player type (e.g. "Warrior", "Mage", "Archer")
     * @param name     The name that was saved in the PlayerSnapshot
     * @return a fresh instance of that subclass, with the given name
     * @throws IllegalArgumentException if the typeName is unrecognized
     */
    public static PlayerCharacter instantiateByType(String typeName, String name) {
        return switch (typeName) {
            case "Warrior" -> new Warrior(name);
            case "Mage"    -> new Mage(name);
            case "Archer"  -> new Archer(name);
            default        -> throw new IllegalArgumentException("Unknown player type: " + typeName);
        };
    }
}
