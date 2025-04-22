package game.items;
import game.characters.PlayerCharacter;
import game.map.Position;
import java.util.Objects;
import java.util.Random;

/**
 * represents the treasure in the game
 */
public class Treasure extends GameItem implements Interactable {

    private int value;
    private boolean collected;

    /**
     * constructs the treasure in the position given in the parameter
     */
    public Treasure(Position position, int value) {
        super(position, "Treasure");
        this.setBlocksMovement(true);
        this.setVisible(false);
        this.value = value;
    }

    /**
     * getter for the value field
     */
    public int getValue() {
        return value;
    }


    /**
     * getter for the collected field, checks if the treasure has been looted
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * displays the symbol for treasure in the map
     */
    @Override
    public String getDisplaySymbol() {
        return "T";
    }


    /**
     * interacting with the treasure
     */
    @Override
    public void interact(PlayerCharacter c) {
        if (collected) {
            return;
        }

        Random rand = new Random();
        int roll = rand.nextInt(6); // 0 to 5

        if (roll == 0) {
            c.addToInventory(new PowerPotion(this.getPosition()));
        }
        else if (roll == 1 || roll == 2 || roll == 3) { // 3/6 → 1/2 chance
            int treasureValue = new Random().nextInt(201) + 100; // 100–300
            c.updateTreasurePoint(treasureValue);
        }
        else {               // Remaining 2/6 → 1/3 chance
            c.addToInventory(new Potion(this.getPosition()));
        }
        collected = true;
    }


    /**
     * method that pickups the item to the players inventory
     */
    @Override
    public boolean pickUp(PlayerCharacter c) {
        if (c == null) {
            return false;
        }
        interact(c);
        return true;
    }


    /**
     * overrides the method in game item class
     */
    @Override
    public boolean isHealingPotion() {return false;}

    /**
     * checks if two treasures are the same in value
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Treasure)) return false;
        if (!super.equals(obj)) return false; // use GameItem.equals()

        Treasure otherT = (Treasure)obj;
        return this.value == otherT.value &&
                this.collected == otherT.collected;
    }

    /**
     * represents the class as string
     */
    public String toString() {
        return super.toString() + "value: " + value + " was it collected?" + collected;
    }

    /**
     * gives a unique hash code for the treasure object
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, collected);
    }

}
