package game.combat;
import game.combat.BattleResult;
import game.characters.Enemy;
import game.characters.PlayerCharacter;


/**
 * Handles resolving combat between any two combatants.
 */
public class CombatSystem {


    /**
     * Resolves combat between an attacker and defender, handling range, evasion, damage, and defeat.
     */
    public static BattleResult resolveCombat(Combatant attacker, Combatant defender) {
        if (attacker == null || defender == null) {return null;}

            int round = 1;
            int totalDamageByAttacker = 0;
            int totalDamageByDefender = 0;

            while(!attacker.isDead() && !defender.isDead()){

                //first Attacker to attack
                int preHp = defender.getHealth();
                performSingleAttack(attacker, defender);
                totalDamageByAttacker += Math.max(0, preHp - defender.getHealth());
                if (defender.isDead()) {
                    if (defender instanceof Enemy enemy) {
                        enemy.defeat();
                    }
                    break;
                }

                //second Attacker to attack
                preHp = attacker.getHealth();
                performSingleAttack(defender, attacker);
                totalDamageByDefender += Math.max(0, preHp - attacker.getHealth());
                if(attacker.isDead()){
                    announceDefeat(attacker);
                    break;
                }
                round ++;
            }

        return new BattleResult( defender.getName() , round , totalDamageByAttacker , totalDamageByDefender);

    }

    private static void performSingleAttack(Combatant atk, Combatant def) {
        if (atk instanceof MeleeFighter melee
                && melee.isInMeleeRange(atk.getPosition(), def.getPosition())) {
            melee.fightClose(def);
        }
        else if (atk instanceof RangedFighter ranged
                && ranged.isInRange(atk.getPosition(), def.getPosition())) {
            ranged.fightRanged(def);
        }
    }

    private static void announceDefeat(Combatant dead) {
        if (dead instanceof Enemy enemy) {
            System.out.println(enemy.enemyDiscription() + " has been defeated!");
            enemy.defeat();
        }
        else if (dead instanceof PlayerCharacter player) {
            System.out.println("Game Over! " + player.getName() + " has been defeated.");
            System.out.println("Total treasure: " + player.getTreasurePoints());
        }
    }
}