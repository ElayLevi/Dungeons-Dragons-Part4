package game.characters;
import java.util.Objects;
import java.util.Random;
import game.combat.Combatant;
import game.core.GameEntity;
import game.map.Position;


/**
 * An abstract base class for all characters in the game, including both players and enemies.
 *
 * <p>
 * This class implements the {@link GameEntity} and {@link Combatant} interfaces,
 * and provides shared logic for position handling, health management, attack power,
 * and evasion mechanics.
 *
 * <p>
 * All subclasses such as {@link PlayerCharacter}, {@link Goblin}, {@link Orc}, and {@link Dragon}
 * extend this class to inherit common combat behavior.
 *
 * <p>
 * Health is clamped between 0 and 100. Attack power is initialized randomly between 4 and 14.
 * The base evasion chance is 25%.
 */
public abstract class AbstractCharacter implements Combatant, GameEntity {

    private Position position;
    private int health;
    private int power;
    private double evasionChance;
    private boolean visible;

    /**
     * constructs a new abstract character with base health and a random power level
     */
    public AbstractCharacter() {
        health = 100;
        Random rand = new Random();
        this.power = rand.nextInt(11) + 4;// 0–10 + 4 → 4–14
        evasionChance = 0.25;
        visible = false;
    }

    /**
     * returns the current position of the character
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * getter for the visible stat
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * returns the health of the abstract character
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * sets the health of the abstract character
     */
    @Override
    public void setHealth(int health) {
        this.health=health;
    }

    /**
     * sets a new position for the character
     */
    public void setPosition(Position p) {
        if (p != null) {
            position = new Position(p);
        }
    }

    /**
     * returns the chance of the abstract character to avoid damage
     */
    @Override
    public double getEvasionChance() {
        return evasionChance;
    }

    /**
     * calculated and returns true if the character evaded, else false
     */
    @Override
    public boolean tryEvade() {
        Random rand = new Random();
        return rand.nextDouble() < evasionChance;
    }

    /**
     * the action of taking damage, stating the source of the damage and amount
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        if (!tryEvade()) {
            health -= amount;
        }
    }

    /**
     * checks if the character is dead or alive
     */
    @Override
    public boolean isDead() {
        return health <= 0;
    }

    /**
     *returns the current Power of the character
     */
    public int getPower() {
        return power;
    }

    /**
     * sets the power level of the character
     */
    public boolean setPower(int p){
        power=p;
        return true;
    }

    /**
     * heals the character according to the amount given in the parameter
     */
    @Override
    public void heal(int amount){
        if(amount>0){
            this.health=Math.min(100,this.health+amount);
        }
    }

    /**
     * represents the class as string
     */
    @Override
    public String toString(){
        return "position: " + position + "power level: " + power + "health: " + health;
    }

    /**
     * checks if the two abstract characters are equal
     */
    @Override
    public boolean equals(Object obj){
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof AbstractCharacter)) {
            return false;
        }

        AbstractCharacter otherCharacter = (AbstractCharacter) obj;
        return this.health == otherCharacter.health && this.power == otherCharacter.power && this.position.equals(otherCharacter.position) && this.evasionChance == otherCharacter.evasionChance;

    }

    /**
     * unique hash code key for an abstract class type
     */
    @Override
    public int hashCode() {
        return Objects.hash(position, health, power);
    }

    /**
     * sets the abstract character as visible or not
     * @param visible , true to make the entity visible or false for invisible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}

