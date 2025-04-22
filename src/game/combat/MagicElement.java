package game.combat;


/**
 * Enum representing magical elements used by spellcasting characters.
 *
 * <p>
 * Each element has a defined strength and weakness relative to other elements.
 * These relationships are used to determine the effectiveness of magic attacks
 * in combat when comparing the attacker's and defender's elements.
 *
 * <p>
 * Elemental advantages follow a circular chain:
 * <ul>
 *     <li>{@code FIRE > ICE}</li>
 *     <li>{@code ICE > LIGHTNING}</li>
 *     <li>{@code LIGHTNING > ACID}</li>
 *     <li>{@code ACID > FIRE}</li>
 * </ul>
 *
 * <p>
 * When casting a spell:
 * <ul>
 *     <li>If the attacker's element is stronger, damage is multiplied by 1.2</li>
 *     <li>If it is weaker, damage is multiplied by 0.8</li>
 *     <li>If the elements are equal, no bonus or penalty is applied</li>
 * </ul>
 *
 */
public enum MagicElement {

    FIRE,ICE,LIGHTNING,ACID;

    /**
     * Determines whether this element is stronger than another element.
     */
    public boolean isStrongerThan(MagicElement other) {
        return (this == FIRE && other == ICE) ||
                (this == ICE && other == LIGHTNING) ||
                (this == LIGHTNING && other == ACID) ||
                (this == ACID && other == FIRE);
    }


    public String toString() {
        return "FIRE,ICE,LIGHTNING,ACID";
    }


}
