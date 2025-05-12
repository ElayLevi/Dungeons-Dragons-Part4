package game.Model.characters;
import game.Model.combat.Combatant;
import game.Model.combat.MagicAttacker;
import game.Model.combat.MagicElement;
import game.Model.combat.RangedFighter;
import game.Model.map.Position;

import java.util.Objects;


/**
 * Represents a ranged magic-using player character.
 *
 * <p>
 * The {@code Mage} class extends {@link PlayerCharacter} and implements both
 * {@link MagicAttacker} and {@link RangedFighter}, allowing it to cast spells
 * and perform long-range attacks. Each mage has a randomly assigned
 * {@link MagicElement}, which influences elemental strength and spell effectiveness.
 *
 * <p>
 * Mages are best used for strategic ranged casting, leveraging elemental advantages
 * to deal high magical damage to enemies.
 *
 * <p>
 * Magic damage is calculated as:
 * <pre>
 * baseDamage = power * 1.5
 * - increased by 20% if element is stronger than the target
 * - decreased by 20% if weaker
 * </pre>
 *
 */
public class Mage extends PlayerCharacter implements MagicAttacker, RangedFighter {

    private MagicElement element;

    /**
     * constructs a mage to the game, with a random element from the known values
     */
    public Mage(String name) {
        super(name);
        MagicElement[] elements = MagicElement.values();
        this.element = elements[new java.util.Random().nextInt(elements.length)]; // randomizes the element of the mage, based on the values in the magic element enum

    }

    /**
     * getter for the element field, returns the type of element of the mage
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
        return self.distanceTo(target) <= getRange();
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
     * overrides from the gameEntity interface
     * @return the symbol of the mage in the game
     */
    @Override
    public String getDisplaySymbol() {
        return "M";
    }

    /**
     * Checks if this mage is equal to another object based on inherited properties and element type.
     */
    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Mage)){
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }
        Mage otherMage = (Mage) obj;
        return this.getElement() == otherMage.getElement();
    }

    /**
     * represents the class as a string
     */
    @Override
    public String toString() {
        return super.toString() + "Mage element: " + this.getElement();
    }

    /**
     * unique hash code key for a mage class type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), element);
    }


}
