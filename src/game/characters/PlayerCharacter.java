package game.characters;
import game.core.Inventory;
import game.items.GameItem;
import game.items.Interactable;
import game.items.Potion;

import java.util.Objects;

/**
 * Represents a playable character controlled by the user.
 *
 * <p>
 * The {@code PlayerCharacter} class extends {@link AbstractCharacter} and adds
 * additional fields and methods related to the player's identity and resources,
 * such as a name, an {@link Inventory}, and treasure points.
 *
 * <p>
 * Player characters are capable of collecting and using items, such as potions
 * and power potions, and accumulating treasure by interacting with objects in the game.
 *
 * <p>
 * Subclasses like {@link Warrior}, {@link Mage}, and {@link Archer} add combat-specific behavior.
 *
 */
public abstract class PlayerCharacter extends AbstractCharacter {
    private String name;
    private Inventory inventory;
    private  int treasurePoints;


    /**
     * constructs a player character with a name and an empty inventory with default stats
     */
    public PlayerCharacter(String name){
        super();
        this.name = name;
        this.inventory = new Inventory();
        treasurePoints = 0;
        setVisible(true);
    }
    /**
     * returns the name of the player character
     */
    public String getName() {
        return this.name;
    }

    /**
     * adds an item to the player's inventory
     */
    public boolean addToInventory(GameItem item){
        if (item == null) {
            return false;
        }
        inventory.AddItem(item);
        return true;
    }
    /**
     * checks if the player can use a potion and uses it
     */
    public boolean usePotion(){
        for (GameItem item : inventory.getItems()){
            if (item.isHealingPotion() && item instanceof Interactable interactable){
                interactable.interact(this);
                inventory.RemoveItem(item);
                return true;
            }
        }
        return false;
    }
    /**
     * checks if the player can use a power potion and uses it
     */
    public boolean UsePowerPotion(){
        for (GameItem item : inventory.getItems()) {
            if (item.isPowerPotion() && item instanceof Interactable interactable) {
                interactable.interact(this);
                inventory.RemoveItem(item);
                return true;
            }
        }
        return false;
    }
    /**
     * getter for the inventory of the player
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * update the current amount of treasure points that the character have.
     */
    public boolean updateTreasurePoint(int amount){
        if (this.treasurePoints + amount < 0) {
            return false;
        }
        treasurePoints += amount;
        return true;
    }
    /**
     * getter for the current amount of treasure points that the character has.
     */
    public int getTreasurePoints(){
        return treasurePoints;
    }


    /**
     * represents the class as string.
     */
    @Override
    public String toString(){
        return super.toString() + "Name" +name + "Inventory" + inventory +"TreasurePoints" +treasurePoints;
    }

    /**
     *checks if the two player characters are equal.
     */
    @Override
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if (!(obj instanceof PlayerCharacter)){
            return false;
        }
        if (!super.equals(obj)) return false;

        PlayerCharacter otherCharacter = (PlayerCharacter)obj;
        return this.name == otherCharacter.name && this.inventory.equals(otherCharacter.inventory) && this.treasurePoints == otherCharacter.treasurePoints;
    }

    /**
     * unique hash code key for a player class type.
     */
    @Override
    public int hashCode(){
        return Objects.hash(name,inventory,treasurePoints);
    }
}