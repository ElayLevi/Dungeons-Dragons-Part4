package game.combat;


import game.map.Position;

/**
 * Represents anyone who can fight or receive damage/heal.
 * <p>
 * this interface is implemented by all game characters
 * </p>
 */
public interface Combatant {

    /**
     * returns the health of the combatant
     */
    int getHealth();

    /**
     * sets the current combatant's health
     * @param health amount of health the combatant receives
     */
    void setHealth(int health);

    /**
     * updates the current combatants status based on the source and amount of damage
     * @param amount of damage the current combatant receives
     * @param source the combatant who damaged the current one
     */
    void receiveDamage(int amount, Combatant source);

    /**
     * updates the current combatants health stat
     * @param amount of health the combatant receives
     */
    void heal(int amount);

    /**
     * returns a boolean if the current combatant is alive or dead
     * @return if the combatants health is zero or below then return false, else true
     */
    boolean isDead();

    /**
     * returns the attack power of the combatant
     * @return a positive integer
     */
    int getPower();

    /**
     * checks if the combatant managed to evade an attack
     * @return if successfully evaded returns true, else false
     */
    boolean tryEvade();

    /**
     * returns the position of the character
     */
    Position getPosition();


    /**
     * checks if the combatant has an element, for example for a warrior return null
     */
    MagicElement getElement();


    /**
     * getter for the evasion chances of all combatants
     */
    double getEvasionChance();
    /**
     * getter for the name of character/enemy
     */
    String getName();
    }
