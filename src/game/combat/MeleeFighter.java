package game.combat;

import game.map.Position;

/**
 * represents all the entities who are able to deal melee damage
 * <p>
 * this interface is implemented by the characters who deal melee damage
 * </p>
 */
public interface MeleeFighter {

    /**
     * initiates a close range fight
     * @param target the target of the attack
     */
    void fightClose(Combatant target);

    /**
     * calculates the distance between self and the target
     * @return true if the distance is 1, else false
     */
    boolean isInMeleeRange(Position self,Position target);

}
