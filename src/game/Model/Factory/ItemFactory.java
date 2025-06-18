package game.Model.Factory;

import game.Model.items.*;
import game.Model.map.Position;

public class ItemFactory {
    public static GameItem createItem(String typeName) {
        return switch (typeName) {
            case "Potion"       -> new Potion(new Position(0,0));
            case "PowerPotion"  -> new PowerPotion(new Position(0,0));
            case "Treasure"     -> new Treasure(new Position(0,0), /*value*/ 0);
            case "Wall"         -> new Wall(new Position(0,0));
            default -> throw new IllegalArgumentException("Unknown item type: " + typeName);
        };
    }
}
