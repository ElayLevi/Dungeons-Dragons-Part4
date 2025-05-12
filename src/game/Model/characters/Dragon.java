package game.Model.characters;
import game.Model.characters.*;
import game.Model.combat.*;
import game.Model.map.Position;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a Dragon enemy in the game.
 *
 * <p>
 * The {@code Dragon} class extends {@link Enemy} and implements
 * {@link PhysicalAttacker}, {@link MagicAttacker}, {@link MeleeFighter},
 * and {@link RangedFighter}, allowing it to attack using both physical and magical means.
 *
 * <p>
 * The Dragon uses melee (physical) attacks when the target is within close range,
 * and ranged magical attacks otherwise. Magical attacks are scaled by the dragon's
 * {@link MagicElement}, following the element advantage rules defined in
 * {@link MagicElement#isStrongerThan(MagicElement)}.
 *
 * <p>
 * When performing a magical attack, the base damage is calculated as:
 * <pre>
 * damage = power * 1.5
 * - increased by 20% if the element is stronger than the target's
 * - decreased by 20% if weaker
 * </pre>
 *
 * <p>
 * The dragon is considered a powerful, high-tier enemy due to its hybrid combat capabilities.
 */
public class Dragon extends Enemy implements MeleeFighter, PhysicalAttacker, RangedFighter, MagicAttacker {

    private MagicElement element;

    /**
     * constructs a dragon, with a random magic element
     */
    public Dragon() {
        super();
        MagicElement[] elements = MagicElement.values();
        this.element = elements[new java.util.Random().nextInt(elements.length)]; // randomizes the element of the dragon, based on the values in the magic element enum
    }


    /**
     * getter for the element field, returns the type of element of the dragon
     */
    @Override
    public MagicElement getElement() {
        return element;
    }
    /**
     * calculates and applies magical damage to the target
     */
    @Override
    public void calculateMagicDamage(Combatant target) {
        double damage = getPower() * 1.5;
        MagicElement targetElement = target.getElement();

        if (targetElement != null) {
            if (getElement().isStrongerThan(targetElement)) {
                damage *= 1.2;
            }
            else if (!getElement().equals(targetElement)) {
                damage *= 0.8;
            }
        }

        target.receiveDamage((int) Math.round(damage), this);
    }
    /**
     * casts a spell
     * @param target the combatant who receives the spell
     */
    @Override
    public void castSpell(Combatant target) {
        calculateMagicDamage(target);
    }

    /**
     * compares between the element of self and the element of other and returns true if the element of self is stronger
     */
    @Override
    public boolean isElementStrongerThan(MagicAttacker other) {
        return this.element.isStrongerThan(other.getElement());
    }
    /**
     * Returns the default range for a ranged magic attack.
     *
     * @return the attack range (2 by default)
     */
    @Override
    public int getRange() {
        return 2;
    }


    /**
     * Checks whether the target is within magic attack range.
     */
    @Override
    public boolean isInRange(Position self, Position target) {
        return self.distanceTo(target) == getRange();
    }


    /**
     * Executes a ranged magical attack if the target is within range.
     */
    @Override
    public void fightRanged(Combatant target) {
        if (isInRange(getPosition(), target.getPosition())) {
            castSpell(target);
        }
    }

    /**
     * calculates the distance between self and the target
     * @return true if the distance is 1, else false
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        return self.distanceTo(target) == 1;
    }

    /**
     * Executes a close-range physical attack if the target is within melee range.
     *
     * @param target the playerCharacter to attack
     */
    @Override
    public void fightClose(Combatant target) {
        if (isInMeleeRange(getPosition(), target.getPosition())) {
            int damage = getPower();
            if (isCriticalHit()) {
                damage *= 2;
            }
            target.receiveDamage(damage, this);
        }
    }


    /**
     * Performs a physical melee attack on a target combatant.
     *
     * @param target the enemy to attack
     */
    @Override
    public void attack(Combatant target) {
        if(isInMeleeRange(getPosition(),target.getPosition())) {
            fightClose(target);
        }
        else if (isInRange(getPosition(),target.getPosition())) {
            fightRanged(target);
        }
        else {
            System.out.println("The dragon is too far away too attack!");
        }
    }

    /**
     * determines if you dealt critical damage, meaning double the original damage
     * @return a boolean value if its successful, the probability of returning true is 10%
     */

    @Override
    public boolean isCriticalHit() {
        Random rand = new Random();
        return rand.nextDouble() < 0.1;
    }

    /**
     * overrides from the gameEntity interface
     * @return the symbol of the Dragon in the game
     */
    @Override
    public String getDisplaySymbol() {
        return "D";
    }

    /**
     * Checks if this Dragon is equal to another object based on inherited properties and element type.
     */
    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Dragon)){
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }
        Dragon otherDragon = (Dragon) obj;
        return this.getElement() == otherDragon.getElement();
    }

    /**
     * represents the class as a string
     */
    @Override
    public String toString() {
        return super.toString() + "Dragon element: " + this.getElement();
    }

    /**
     * unique hash code key for a Dragon class type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), element);
    }

    /**
     * describes the type of enemy
     */
    public String enemyDiscription() {
        return "Dragon";
    }
}