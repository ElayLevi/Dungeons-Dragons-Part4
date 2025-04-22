package game.items;
import game.characters.PlayerCharacter;
import game.map.Position;
import java.util.Objects;


/**
 * Represents all the walls in the game
 */
public class Wall extends GameItem {

    private boolean blocksMovement;

    /**
     * constructs a wall at the position given in the parameter
     */
    public Wall(Position position) {
        super(position,"Wall");
        this.setBlocksMovement(true);
        this.setVisible(false);
    }

    /**
     * implements get display symbol from game item abstract class
     */
    public String getDisplaySymbol() {
        return "#";
    }

    /**
     * overrides the method in game item class
     */
    @Override
    public boolean isHealingPotion() {return false;}


    /**
     * represents the class as string
     */
    @Override
    public String toString(){
        return "Wall:" + getPosition();
    }

    /**
     * compares between two different walls if they're the same
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Wall)) return false;
        return super.equals(obj); // use GameItem.equals()
    }

    /**
     * gives a uniques hash code for the wall object
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    public boolean pickUp(PlayerCharacter c) {
        return false;
    }

}
