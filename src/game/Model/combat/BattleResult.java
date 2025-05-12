package game.Model.combat;
import java.util.Objects;
/**
 * Represents the result summary of a single combat encounter.
 *
 * <p>This class stores key statistics about a battle:
 * <ul>
 *   <li>Enemy name</li>
 *   <li>Number of rounds fought</li>
 *   <li>Total damage dealt by the player</li>
 *   <li>Total damage dealt by the enemy</li>
 * </ul>
 */

public class BattleResult {
    private final String enemyName;
    private final int rounds;
    private final int dmgByPlayer;
    private final int dmgByEnemy;

    public BattleResult(String enemyName, int rounds, int dmgByPlayer, int dmgByEnemy) {
        this.enemyName = enemyName;
        this.rounds = rounds;
        this.dmgByPlayer = dmgByPlayer;
        this.dmgByEnemy = dmgByEnemy;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public int getRounds() {
        return rounds;
    }

    public int getDmgByPlayer() {
        return dmgByPlayer;
    }

    public int getDmgByEnemy() {
        return dmgByEnemy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleResult)) return false;
        BattleResult other = (BattleResult) o;
        return rounds == other.rounds
                && dmgByPlayer == other.dmgByPlayer
                && dmgByEnemy == other.dmgByEnemy
                && Objects.equals(enemyName, other.enemyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enemyName, rounds, dmgByPlayer, dmgByEnemy);
    }

    @Override
    public String toString() {
        return String.format(
                "Battle vs %s: rounds=%d, dmgByPlayer=%d, dmgByEnemy=%d",
                enemyName, rounds, dmgByPlayer, dmgByEnemy
        );
    }
}
