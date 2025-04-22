package game.items;
import game.characters.PlayerCharacter;
import game.core.GameEntity;
import game.map.Position;
import java.util.Objects;

/**
 * Represents all the different kinds of items in the game, it implements the game entity interface because items are entities
 */
public abstract class GameItem implements GameEntity {

    private Position position;
    private boolean blocksMovement;
    private String description;
    private boolean visible;

    /**
     * Constructs a default GameItem at position (0, 0) with no description.
     */
    public GameItem() {
        position = new Position(0,0);
        blocksMovement = true;
        description = "";
        visible = false;
    }

    /**
     * checks if the item is a healing potion
     */
    public abstract boolean isHealingPotion();

    /**
     * Constructs a GameItem with a given position and description.
     *
     * @param position the position of the item on the map
     * @param description the textual description of the item
     */
    public GameItem(Position position, String description) {
        this.position = new Position(position);
        this.description = description;
    }

    /**
     * get method for blocks movement
     */
    public boolean getBlocksMovement() {
        return blocksMovement;
    }

    /**
     * setter for blocks movement
     */
    protected void setBlocksMovement(boolean other) {
        blocksMovement = other;
    }

    /**
     * setter for description
     */
    protected void setDescription(String other) {
        description = other;
    }

    /**
     * get method for description field
     */
    public String getDescription() {
        return description;
    }

    /**
     * get method for visible field
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * implements the get position method in the game entity interface, returns the position of the item
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * implements the set position method in the game entity interface
     * returns boolean if the position was successfully set
     */
    @Override
    public void setPosition(Position newPos) {
        position = new Position(newPos);
    }

    /**
     * Subclasses must provide their own visual symbol for display.
     */
    @Override
    public abstract String getDisplaySymbol();

    /**
     * implements the set visible method in the game entity interface
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * equals method for game item
     * @param obj the other game item I want to compare
     * @return true if they're equal in value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GameItem)) return false;

        GameItem otherItem = (GameItem)obj;
        return this.position.equals(otherItem.position) &&
                this.blocksMovement == otherItem.blocksMovement &&
                this.description.equals(otherItem.description) &&
                this.visible == otherItem.visible;
    }

    /**
     * the string representation for the class
     */
    @Override
    public String toString() {
        return "Item: " + description + " at " + position.toString();
    }

    /**
     * the specific hashcode for the object, in the future it will be better when you run in sets or maps
     */
    @Override
    public int hashCode() {
        return Objects.hash(position, blocksMovement, description, visible);
    }


    /**
     * Indicates whether this item is a regular power potion
     */
    public boolean isPowerPotion() {
        return false;
    }


    /**
     * abstract method that pickups the item to the players inventory
     */
    public abstract boolean pickUp(PlayerCharacter c);

}
