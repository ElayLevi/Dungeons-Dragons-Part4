package game.combat;

/**
 * represents all the entities who are capable of dealing magic damage
 * <p>
 * this interface is implemented by the characters who deal magic damage
 * </p>
 */
public interface MagicAttacker {

    /**
     * calculates how much damage is dealt, depends on the targets magic elements level
     * if the targets magic element is lower from the attacker, the damage is multiplied by 1.2
     * if the targets magic element is higher than the attacker, the damage is multiplied by 0.8
     */
    void calculateMagicDamage(Combatant target);

    /**
     * casts a spell on a target
     * @param target the combatant who receives the spell
     */
    void castSpell(Combatant target);

    /**
     *  returns the type of element of the attacker
     */
    MagicElement getElement();

    /**
     * checks if the targets magic element is stronger or weaker from the attacker
     * @param other the target you compare with
     * @return true if the attackers element is stronger, else return false
     */
    boolean isElementStrongerThan(MagicAttacker other);
}
