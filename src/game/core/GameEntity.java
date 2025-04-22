package game.core;
import game.map.Position;

/**
 * Represents any entity that exists on the game map and has a visual representation.
 *<p>
 * This interface is implemented by all game objects that occupy a position on the map,
 * including characters, enemies, and items. It provides basic spatial and visibility
 * control, enabling consistent handling of entities across the game world.
 * </p>
 */
public interface GameEntity {

    /**
     * returns the current position of the entity on the map
     */
    Position getPosition();

    /**
     * updates the current entity's position on the map
     * @param newPos the new position for the current entity
     */
    void setPosition(Position newPos);

    /**
     * returns the symbol used to visually represent the entity on the map
     */
    String getDisplaySymbol();

    /**
     * sets the visibility status of the entity
     * @param visible , true to make the entity visible or false for invisible
     */
    void setVisible(boolean visible);

}
