package game.characters;
import game.items.Treasure;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a non-player enemy character in the game.
 *
 * <p>
 * The {@code Enemy} class extends {@link AbstractCharacter} and serves as a base
 * class for all enemy types (e.g., {@link Goblin}, {@link Orc}, {@link Dragon}).
 * It includes shared properties and behaviors such as loot generation and
 * defeat-handling mechanics.
 */
public abstract class Enemy extends AbstractCharacter {

    private int loot;

    /**
     * Constructs a new enemy with 50 health and randomized loot between 100 and 300.
     */
    public Enemy() {
        super();
        setHealth(50);
        this.loot = new Random().nextInt(201) + 100; // 100–300
    }


    /**
     * Returns the loot value that this enemy carries.
     */
    public int getLoot() {
        return loot;
    }


    /**
     * Handles logic when the enemy is defeated.
     */
    public Treasure defeat() {
        return new Treasure(getPosition(),loot);
    }


    /**
     * Checks if this enemy is equal to another object based on loot and inherited fields.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if (!(obj instanceof Enemy)){
            return false;
        }
        if (!super.equals(obj)) return false;

        Enemy otherEnemy = (Enemy) obj;

        return (this.loot == otherEnemy.loot);
    }

    /**
     * represents the class as a string
     */
    @Override
    public String toString() {
        return super.toString() + "Enemy's loot:" + getLoot();
    }

    /**
     * unique hash code key for an enemy type
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLoot());
    }

    /**
     * describes the type of enemy
     */
    public abstract String enemyDiscription();

    /**
     * returns the name of the player character
     */
    public String getName() {
        return enemyDiscription();
    }

}
