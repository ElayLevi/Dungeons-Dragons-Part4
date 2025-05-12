package game.Model.characters;
import game.Model.characters.*;
import game.Model.combat.*;
import game.Model.map.Position;
import java.util.Objects;
import java.util.Random;
/**
 * Represents an orc - enemy character in the game.
 *
 * <p>
 * The {@code Orc} class extends {@link Enemy}  and implements
 * both {@link PhysicalAttacker} and {@link MeleeFighter}.
 */
public class Orc extends Enemy implements PhysicalAttacker , MeleeFighter {

    private  double resistance;

    /**
     * constructs an orc with a resistance ability between 0-0.5
     */
    public Orc(){
        super();
        this.resistance=new Random().nextDouble() * 0.5;//0-0.5
    }


    /**
     * Receives damage from a Mage and applies defense-based resistance.
     * If the attack is not evaded, the incoming damage is reduced by a
     * factor based on the orc's resistance.
     *
     * @param amount the base amount of incoming damage
     * @param source the combatant dealing the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        if (!tryEvade()){
            if(source instanceof MagicAttacker){
                amount *= (1-resistance);
            }
            super.receiveDamage(amount, source);
        }
        else {
            System.out.println("The orc evaded the attack!");
        }
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
     * calculates the distance between self and the target
     * @return true if the distance is 1, else false
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
     * Performs a physical melee attack on a target combatant.
     *
     * @param target the playerCharacter to attack
     */
    @Override
    public void attack(Combatant target) {
        fightClose(target);
    }


    /**
     * checks if the combatant has an element, for example for a warrior return null
     */
    @Override
    public MagicElement getElement() {
        return null;
    }


    /**
     * returns the symbol used to visually represent the entity on the map
     */
    @Override
    public String getDisplaySymbol () {
        return "O";
    }


    /**
     *checks if the two orcs characters are equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if (!(obj instanceof Orc)){
            return false;
        }
        if (!super.equals(obj)) return false;

        Orc otherOrc = (Orc) obj;

        return (this.resistance == otherOrc.resistance);
    }


    /**
     * getter for the resistance field for the orc
     */
    public double getResistance(){
        return resistance;
    }


    /**
     * represents the class as a string
     */
    @Override
    public String toString() {
        return super.toString() + "Orc's resistance:" + getResistance() ;
    }


    /**
     * unique hash code key for an orc type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getResistance());
    }

    /**
     * describes the type of enemy
     */
    public String enemyDiscription() {
        return "Orc";
    }
}
