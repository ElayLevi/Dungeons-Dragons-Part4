package game.Model.memento;

import game.Model.characters.PlayerCharacter;
import game.Model.items.GameItem;
import game.Model.map.Position;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A serializable snapshot of a PlayerCharacter's state,
 * including its concrete subclass name and inventory items.
 */
public class PlayerSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String typeName;
    private final String playerName;
    private final Position pos;
    private final int health;
    private final int power;
    private final int treasurePoints;
    private final double evasionChance;

    // NEW: Store inventory items
    private final List<String> inventoryItemTypes;

    /** snapshot constructor */
    public PlayerSnapshot(PlayerCharacter p) {
        this.typeName       = p.getClass().getSimpleName();
        this.playerName     = p.getName();
        this.pos            = new Position(p.getPosition());
        this.health         = p.getHealth();
        this.power          = p.getPower();
        this.treasurePoints = p.getTreasurePoints();
        this.evasionChance  = p.getEvasionChance();

        // Capture inventory items
        this.inventoryItemTypes = new ArrayList<>();
        for (GameItem item : p.getInventory().getItems()) {
            inventoryItemTypes.add(item.getClass().getSimpleName());
        }
    }

    /** full deserialization ctor (if you really need it) */
    public PlayerSnapshot(String typeName,
                          String playerName,
                          Position pos,
                          int health,
                          int power,
                          int treasurePoints,
                          double evasionChance,
                          List<String> inventoryItemTypes)
    {
        this.typeName       = typeName;
        this.playerName     = playerName;
        this.pos            = new Position(pos);
        this.health         = health;
        this.power          = power;
        this.treasurePoints = treasurePoints;
        this.evasionChance  = evasionChance;
        this.inventoryItemTypes = new ArrayList<>(inventoryItemTypes);
    }

    public String   getTypeName()        { return typeName; }
    public String   getPlayerName()      { return playerName; }
    public Position getPosition()        { return new Position(pos); }
    public int      getHealth()          { return health; }
    public int      getPower()           { return power; }
    public int      getTreasurePoints()  { return treasurePoints; }
    public double   getEvasionChance()   { return evasionChance; }
    public List<String> getInventoryItemTypes() { return new ArrayList<>(inventoryItemTypes); }
}