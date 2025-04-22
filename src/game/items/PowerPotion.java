package game.items;
import game.characters.PlayerCharacter;
import game.map.Position;
import java.util.Objects;
import java.util.Random;

/**
 * represents the power potions in the game
 */
public class PowerPotion extends Potion{

    /**
     * Constructs a new Potion at the specified position with a random healing amount.
     */
    public PowerPotion(Position position) {
        super(position);
        this.setIncreaseAmount(new Random().nextInt(5) + 1); // 1-5
        this.setDescription("Power Potion");
    }

    /**
     * overrides the apply effects method from super class potion, that way when we use polymorphism it uses this apply effects method in the interact method in potion
     */
    @Override
    protected void applyEffect(PlayerCharacter c) {
        c.setPower(c.getPower() + getIncreaseAmount());
    }


    /**
     *  checks if the potions are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PowerPotion)) return false;
        return super.equals(obj);
    }

    /**
     * overrides the method in game item class
     */
    @Override
    public boolean isPowerPotion() {
        return true;
    }

    /**
     * overrides the method in game item class
     */
    @Override
    public boolean isHealingPotion() {return false;}

    /**
     * represents the class as a string
     */
    public String toString() {
        return "Power potion" + "Amount: " + getIncreaseAmount() + "used? " + getPotionCondition();
    }

    /**
     * gives the potion a unique hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIncreaseAmount(), getPotionCondition());
    }

    /**
     * Returns the display symbol used for this item on the game map.
     */
    @Override
    public String getDisplaySymbol() {
        return "+";
    }

}
