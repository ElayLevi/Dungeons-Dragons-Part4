package game.Model.items;
import game.Model.map.Position;
import java.util.Random;
import game.Model.characters.PlayerCharacter;
import java.util.Objects;

/**
 * represents the health potions in the game
 */
public class Potion extends GameItem implements Interactable {

    private int increaseAmount;
    private boolean isUsed;

    /**
     * Constructs a new Potion at the specified position with a random healing amount.
     */
    public Potion(Position position) {
        super(position, "Health potion");
        this.increaseAmount = new Random().nextInt(41) + 10; // 10–50
        this.isUsed = false;
        this.setVisible(false);
        this.setBlocksMovement(true);
    }

    /**
     * getter for isUsed field, checks if the potion was used or not
     */
    public boolean getPotionCondition() {
        return isUsed;
    }

    /**
     * setter for isUsed field, changes if the potion was used or not
     */
    public void setIsPotionUsed(boolean other) {
        isUsed = other;
    }


    /**
     * getter for increased amount field, Returns the healing value of this potion.
     */
    public int getIncreaseAmount() {
        return increaseAmount;
    }

    /**
     * setter for increased amount
     */
    protected void setIncreaseAmount(int other) {
        increaseAmount = other;
    }

    /**
     * applies the effect of the potion
     */
    protected void applyEffect(PlayerCharacter c) {
        c.heal(Math.min(increaseAmount, 100 - c.getHealth()));
    }

    /**
     * Interacts with a PlayerCharacter, attempting to heal them.
     * If the potion hasn't been used yet, it heals the character and becomes inactive.
     */
    @Override
    public void interact(PlayerCharacter c) {
        if (!isUsed) {
            applyEffect(c);
            isUsed = true;
        }
    }

    /**
     * Returns the display symbol used for this ite m on the game map.
     */
    @Override
    public String getDisplaySymbol() {
        return "P";
    }

    /**
     * checks if two potions are the same in value, overriding the object value method
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Potion)) return false;
        if (!super.equals(obj)) return false; // use GameItem.equals()

        Potion otherP = (Potion) obj;
        return this.increaseAmount == otherP.increaseAmount &&
                this.isUsed == otherP.isUsed;
    }

    /**
     * represents the class as a string
     */
    public String toString() {
        return super.toString() + "amount: " + increaseAmount + "is it used? " + isUsed;
    }

    /**
     * gives the potion a unique hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), increaseAmount, isUsed);
    }

    /**
     * overrides the method in game item class
     */
    @Override
    public boolean isHealingPotion() {
        return true;
    }

    /**
     * method that pickups the item to the players inventory
     */
    public boolean pickUp(PlayerCharacter c) {
        if (c == null) {
            return false;
        }
        c.addToInventory(this);
        return true;
    }

}
