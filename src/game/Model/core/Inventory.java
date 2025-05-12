package game.Model.core;

import game.Model.items.GameItem;
import java.util.ArrayList;
import java.util.List;


/**
 *  Represents the list of items that each character have in his inventory
 */
public class Inventory {

    private List<GameItem> items;

    /**
     *default constructor.
     */
    public Inventory() {
        this.items = new ArrayList<>();
    }

    /**
     * add an item to the items list in the characters inventory.
     */
    public boolean AddItem(GameItem item) {
        if (item != null) {
            return items.add(item);
        }
        return false;
    }

    /**
     * checks if the specific item is in the items list of a characters inventory.
     */
    public boolean Contains(GameItem item) {
        return this.items.contains(item);
    }

    /**
     *checks if the specific item is in the items list of a characters inventory and removes it.
     */
    public boolean RemoveItem(GameItem item) {
        if (Contains(item)) {
            items.remove(item);
            return true;
        }
        return false;
    }

    /**
     *getter for the item list inventory.
     */
    public List<GameItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * represents the class as a string
     */
    @Override
    public String toString() {
        return "Inventory" + "Items :" + items;
    }

    /**
     * compares between two inventories
     */
    @Override
    public boolean equals(Object obj){
        boolean ans = false;
        if (obj instanceof Inventory) {
            ans = (this.items.equals(((Inventory) obj).items));
        }
        return ans;
    }
}
