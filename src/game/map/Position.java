package game.map;
import java.util.Objects;

/**
 * Represents a coordinate on the game map using row and column values.
 *
 * <p>
 * Positions are used by all game entities to determine their location within
 * the game world. This class provides utility methods for calculating distance
 * and comparing positions.
 *
 * <p>
 * Distance is calculated using the Manhattan method.
 */
public class Position {

    private int row;
    private int col;

    /**
     * Construct a new position based on the specified row and col
     * @param row the row coordinates
     * @param col the column coordinates
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Copy constructor for Position
     */
    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    /**
     * getter for the col field
     * @return the column coordinate
     */
    public int getCol() {
        return col;
    }

    /**
     * getter for the row field
     * @return the row coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * Calculates the Manhattan distance between this position and another.
     *
     * @param other the other Position
     * @return the Manhattan distance between the two positions
     */
    public int distanceTo(Position other) {
        if (other == null) {
            throw new IllegalArgumentException("The position of the target is not valid");
        }
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    /**
     * checks you if the position of both objects are the same
     * @param obj the other objects position
     * @return true if both objects are in the same coordinates, else false
     */
    @Override
    public boolean equals(Object obj) {
        boolean ans = false;
        if (obj instanceof Position) {
            ans = (this.row == ((Position)obj).row) && (this.col == ((Position)obj).col);
        }
        return ans;
    }

    /**
     * represents the class as a string
     * @return string representation
     */
    @Override
    public String toString() {
        return "row: " + getRow() + "column: " + getCol();
    }

    @Override public int hashCode() {
        return Objects.hash(row, col);
    }
}
