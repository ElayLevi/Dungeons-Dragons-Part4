package game.combat;
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
    private  String enemyName;
    private int rounds;
    private int dmgByPlayer;
    private int dmgByEnemy;


    /**
     * Constructs a new BattleResult.
     *
     * @param enemyName    the name of the enemy fought
     * @param rounds       the number of rounds in the battle
     * @param dmgByPlayer  total damage dealt by the player
     * @param dmgByEnemy   total damage dealt by the enemy
     */

    public BattleResult(String enemyName , int rounds , int dmgByPlayer , int dmgByEnemy){
        this.enemyName = enemyName;
        this.rounds = rounds;
        this.dmgByPlayer = dmgByPlayer;
        this.dmgByEnemy = dmgByEnemy;
    }
    /**
     * Returns the name of the enemy.
     *
     * @return enemy name
     */
    public String getEnemyName() {
        return enemyName;
    }

    /**
     * Returns the total number of rounds fought.
     *
     * @return number of rounds
     */
    public int getRounds() {
        return rounds;
    }
    /**
     * Returns the total damage dealt by the player.
     *
     * @return damage dealt by the player
     */
    public int getDmgByPlayer() {
        return dmgByPlayer;
    }

    /**
     * Returns the total damage dealt by the enemy.
     *
     * @return damage dealt by the enemy
     */
    public int getDmgByEnemy() {
        return dmgByEnemy;
    }

    /**
     * checks if the two battle results are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BattleResult)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        BattleResult otherResult = (BattleResult) obj;
        return this.enemyName == otherResult.enemyName && this.dmgByEnemy == otherResult.dmgByEnemy && this.dmgByPlayer == otherResult.dmgByPlayer && this.rounds == otherResult.rounds;
    }


    /**
     * represents the class as string
     */
    @Override
    public String toString(){
        return "enemy name: " + enemyName + "number of rounds" + rounds;
    }

}
