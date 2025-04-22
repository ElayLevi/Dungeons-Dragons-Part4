package game.combat;

import game.map.Position;

/**
 * represents all the entities who are able to deal ranged damage
 * <p>
 * this interface is implemented by the characters who deal ranged damage
 * </p>
 */
public interface RangedFighter {

    /**
     * initiates a ranged fight
     * @param target the combatant getting attacked
     */
    void fightRanged(Combatant target);

    /**
     * returns the range capability of the attacker
     * @return an integer that will represent the range that the attacker can reach
     */
    int getRange();

    /**
     * calculates the distance between self and the target
     * @return true if the distance is lower or equal to the attackers range (default 2), else false
     */
    boolean isInRange(Position self, Position target);
}
