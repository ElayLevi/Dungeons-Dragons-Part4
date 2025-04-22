package game.characters;
import game.combat.Combatant;
import game.combat.MagicElement;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.map.Position;
import java.util.Objects;
import java.util.Random;


/**
 * Represents a Goblin enemy in the game.
 *
 * <p>
 * The {@code Goblin} class extends {@link Enemy} and implements
 * {@link PhysicalAttacker} and {@link MeleeFighter}, allowing it to
 * engage in close-range physical combat.

 */
public class Goblin extends Enemy implements MeleeFighter, PhysicalAttacker {

    private int agility;

    /**
     * constructs a goblin based on the Enemy default constructor with a random agility between 0-80
     */
    public Goblin() {
        super();
        this.agility = new Random().nextInt(81); // 0-80
    }


    /**
     * getter for the agility field of the goblin
     */
    public int getAgility() {
        return agility;
    }

    /**
     * evades the enemy considering his agility
     */
    @Override
    public boolean tryEvade() {
        double goblinEvasion = Math.min(0.8, agility / 100.0);
        return new Random().nextDouble() < goblinEvasion;

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
     * determines if you dealt critical damage, meaning double the original damage
     * @return a boolean value if its successful, the probability of returning true is 10%
     */
    @Override
    public boolean isCriticalHit() {
        Random rand = new Random();
        return rand.nextDouble() < 0.1;
    }
    /**
     * initiates a close range fight
     * @param target the target of the attack
     */
    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition())) {
            int damage = getPower();
            if(isCriticalHit()) {
                damage *=2;
            }
            target.receiveDamage(damage, this);
        }
    }

    /**
     * attack a combatant (deals damage)
     * @param target the combatant you attack
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
        return "G";
    }

    /**
     * Checks if this goblin is equal to another object based on the inherited fields.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Goblin)){
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }

        Goblin otherGoblin = (Goblin)obj;

        return (this.agility == otherGoblin.agility);
    }

    /**
     * represents the class as string
     */
    @Override
    public String toString(){
        return super.toString() + "agility: " + agility;
    }

    /**
     * unique hash code key for a goblin class type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), agility);
    }

    /**
     * describes the type of enemy
     */
    public String enemyDiscription() {
        return "Goblin";
    }
}


