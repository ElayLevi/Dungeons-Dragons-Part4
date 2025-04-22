package game.items;

import game.characters.PlayerCharacter;

/**
 * represents all the objects that you can interact with
 * <p>
 * this interface is implemented by items
 * </p>
 */
public interface Interactable {

    /**
     * the action preformed once a character interacts with the object
     * @param c character who preforms the interaction
     */
    void interact(PlayerCharacter c);
}
