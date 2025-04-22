package game.combat;


/**
 * represents all the entities who can deal physical damage (sword,mace,club...)
 * <p>
 * this interface is implemented by characters who can deal physical damage
 * </p>
 */
public interface PhysicalAttacker {

    /**
     * attack a combatant (deals damage)
     * @param target the combatant you attack
     */
    void attack(Combatant target);

    /**
     * determines if you dealt critical damage, meaning double the original damage
     * @return a boolean value if its successful, the probability of returning true is 10%
     */
    boolean isCriticalHit();

}
