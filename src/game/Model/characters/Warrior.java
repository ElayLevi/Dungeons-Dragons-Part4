package game.Model.characters;
import game.Model.combat.Combatant;
import game.Model.combat.MagicElement;
import game.Model.combat.MeleeFighter;
import game.Model.combat.PhysicalAttacker;
import game.Model.map.Position;
import java.util.Objects;
import java.util.Random;


/**
 * Represents a melee-focused player character with enhanced defense.
 *
 * <p>
 * The {@code Warrior} class extends {@link PlayerCharacter} and implements
 * both {@link PhysicalAttacker} and {@link MeleeFighter}, enabling it to
 * perform close-range physical attacks.
 *
 * <p>
 * Warriors have a defense stat that reduces incoming damage by up to 60%.
 * The actual damage taken is calculated based on the attacking power and
 * the warrior's defense using the formula:
 * <pre>
 * reducedDamage = enemyPower * (1 - min(0.6, defence / 200.0))
 * </pre>
 *
 * <p>
 * Warriors are ideal for players who prefer close-range combat and higher durability.
 *
 */
public class Warrior extends PlayerCharacter implements MeleeFighter, PhysicalAttacker {

    private int defence;

    /**
     * constructs a warrior with the name given in the parameter and a random defence between 0-120
     */
    public Warrior(String name) {
        super(name);
        this.defence = new Random().nextInt(121); // 0-120
    }


    /**
     * Returns the warrior's defense value.
     *
     * @return the defense stat (0–120)
     */
    public int getDefence() {
        return defence;
    }


    /**
     * Receives damage from an enemy and applies defense-based reduction.
     * If the attack is not evaded, the incoming damage is reduced by a
     * factor based on the warrior's defense.
     *
     * @param amount the base amount of incoming damage
     * @param source the combatant dealing the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        if (!tryEvade()) {
            double reduction = Math.min(0.6, defence / 200.0);
            int reducedDamage = (int) Math.round(amount * (1 - reduction));
            super.receiveDamage(reducedDamage, source);
        }
    }


    /**
     * Performs a physical melee attack on a target combatant.
     *
     * @param target the enemy to attack
     */
    @Override
    public void attack(Combatant target) {
        fightClose(target);
    }


    /**
     * Executes a close-range physical attack if the target is within melee range.
     *
     * @param target the enemy to attack
     */
    @Override
    public void fightClose(Combatant target) {
        if (isInMeleeRange(getPosition(), target.getPosition())) {
            int damage = getPower();
            if(isCriticalHit()) {
                damage *=2;
            }
            target.receiveDamage(damage, this);
        }
    }


    /**
     * checks if the warrior is in sufficient range to the target to attack
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        return self.distanceTo(target) == 1;
    }


    /**
     * checks the odds and returns true if the odds are 10%
     */
    @Override
    public boolean isCriticalHit() {
        Random rand = new Random();
        return rand.nextDouble() < 0.1;
    }

    /**
     * returns the symbol used to visually represent the entity on the map
     */
    @Override
    public String getDisplaySymbol () {
        return "W";
    }

    /**
     * represents the class as string
     */
    @Override
    public String toString(){
        return super.toString() + "defence" + defence;
    }

    /**
     * checks if the two warrior characters are equal
     */
    @Override
    public boolean equals(Object obj){
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Warrior)){
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }
        Warrior otherWarrior = (Warrior) obj;
        return this.defence == otherWarrior.defence;
    }

    /**
     * unique hash code key for a warrior class type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), defence);
    }

    /**
     * confirms that the warrior has no element
     */
    @Override
    public MagicElement getElement() {
        return null;
    }

}