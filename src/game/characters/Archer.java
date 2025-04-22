package game.characters;
import game.combat.*;
import game.map.Position;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a ranged Archer player character.
 *
 * <p>
 * The {@code Archer} class extends {@link PlayerCharacter} and implements both
 * {@link PhysicalAttacker} and {@link RangedFighter}, allowing it to
 * perform long-range attacks by shot arrows.
 * <p>
 * Archers are best used for long ranged shots, known for high accuracy capability.
 *
 */
public class Archer extends PlayerCharacter implements PhysicalAttacker , RangedFighter {
    private double accuracy;


    /**
     * constructs an archer with a name, and with a random accuracy
     */
    public Archer(String name) {
        super(name);
        Random rand = new Random();
        accuracy = rand.nextDouble() * 0.8;
    }
    /**
     * checks if the Archers attack attempt on the target is successful
     */
    public boolean attemptHit(Combatant enemy) {
        double effectiveEvasion = enemy.getEvasionChance() * (1 - accuracy);
        return accuracy > effectiveEvasion;
    }
    /**
     * checks if the Archer is in sufficient range to the target to attack
     */
    @Override
    public void fightRanged(Combatant target) {
        if (isInRange(getPosition(), target.getPosition())) {
            if (attemptHit(target)) {
                int damage = getPower();
                if (isCriticalHit()) {
                    damage *= 2;
                }
                target.receiveDamage(damage, this);
            }
        }
    }
    /**
     * Performs a Ranged attack on a target combatant.
     *
     * @param target the enemy to attack
     */
    @Override
    public void attack(Combatant target) {
        fightRanged(target);
    }


    /**
     * checks if the enemy is at archer range.
     */
    public boolean isInRange(Position self, Position target) {
        return self.distanceTo(target) <= getRange();
    }

    /**
     * get the archers range.
     */
    @Override
    public int getRange(){
        return 2;
    }
    /**
     * checks the odds if you got a critical hit
     */
    @Override
    public boolean isCriticalHit() {
        return new Random().nextDouble() < (accuracy / 2); // scale crit chance by accuracy
    }


    /**
     * get the archer Element(if he has one).
     */
    @Override
    public MagicElement getElement() {
        return null;
    }

    /**
     * represents the class as string.
     */
    @Override
    public String toString() {
        return super.toString() + "accuracy" + accuracy;
    }

    /**
     * checks if the two Archer characters are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Archer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Archer otherArcher = (Archer) obj;
        return this.accuracy == otherArcher.accuracy;
    }

    /**
     * unique hash code key for an Archer class type.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accuracy);
    }
    /**
     * overrides from the gameEntity interface
     * @return the symbol of the archer in the game
     */
    @Override
    public String getDisplaySymbol() {
        return "A";
    }
}