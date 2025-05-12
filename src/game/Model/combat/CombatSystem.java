package game.Model.combat;
import game.Model.combat.BattleResult;
import game.Model.combat.MeleeFighter;
import game.Model.combat.RangedFighter;
import game.Model.combat.Combatant;
/**
 * Handles resolving combat between any two combatants.
 */
public class CombatSystem {


    /**
     * Resolves combat between an attacker and defender, handling range, evasion, damage, and defeat.
     */
    public static BattleResult resolveCombat(Combatant attacker, Combatant defender) {
        if (attacker == null || defender == null) return null;

        int rounds = 0;
        int totalDamageByAttacker = 0;
        int totalDamageByDefender = 0;

        while (!attacker.isDead() && !defender.isDead()) {
            rounds++;

            // attacker strikes defender
            int preHp = defender.getHealth();
            performSingleAttack(attacker, defender);
            totalDamageByAttacker += Math.max(0, preHp - defender.getHealth());
            if (defender.isDead()) break;

            // defender strikes back
            preHp = attacker.getHealth();
            performSingleAttack(defender, attacker);
            totalDamageByDefender += Math.max(0, preHp - attacker.getHealth());
            if (attacker.isDead()) break;
        }

        return new BattleResult(
                defender.getName(),
                rounds,
                totalDamageByAttacker,
                totalDamageByDefender
        );
    }

    private static void performSingleAttack(Combatant atk, Combatant def) {
        if (atk instanceof MeleeFighter melee
                && melee.isInMeleeRange(atk.getPosition(), def.getPosition())) {
            melee.fightClose(def);
        } else if (atk instanceof RangedFighter ranged
                && ranged.isInRange(atk.getPosition(), def.getPosition())) {
            ranged.fightRanged(def);
        }
    }
}