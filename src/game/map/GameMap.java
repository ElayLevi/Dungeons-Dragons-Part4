package game.map;
import game.characters.Dragon;
import game.characters.Goblin;
import game.characters.Orc;
import game.characters.PlayerCharacter;
import game.core.GameEntity;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Wall;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Represents the game map, encapsulating the layout and terrain of the game world.
 *
 * <p>
 * The {@code GameMap} class manages the spatial representation of the game environment,
 * including the positions of entities, terrain types, and other relevant features.
 * It provides methods to query and manipulate the state of the map, facilitating
 * interactions such as movement, collision detection, and rendering.
 * </p>
 *
 * <p>
 * Typical responsibilities of this class may include:
 * </p>
 * <ul>
 *   <li>Storing the dimensions and structure of the map grid.</li>
 *   <li>Tracking the placement of entities and items within the map.</li>
 *   <li>Providing methods to determine valid movement paths and interactions.</li>
 *   <li>Handling updates to the map state as the game progresses.</li>
 * </ul>
 *
 * <p>
 * The design of {@code GameMap} should ensure efficient access and modification
 * of map data to support real-time game mechanics.
 * </p>
 *
 */
public class GameMap {

    private Map <Position, List<GameEntity>> grid;
    private int row;
    private int col;

    /**
     * Constructs an empty GameMap.
     */
    public GameMap(int row, int col, PlayerCharacter player) {
        if (row < 10 || col < 10) {
            throw new IllegalArgumentException("Map must be at least 10x10");
        }

        this.row = row;
        this.col = col;
        this.grid = new HashMap<>();
        Random rand = new Random();

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                double roll = rand.nextDouble();
                Position pos = new Position(r, c);

                if (roll < 0.4) {
                    continue;
                } else if (roll < 0.7) {
                    GameEntity enemy = switch (rand.nextInt(3)) {
                        case 0 -> new Goblin();
                        case 1 -> new Orc();
                        default -> new Dragon();
                    };
                    addEntity(pos, enemy);
                } else if (roll < 0.8) {
                    // 10% chance to add a wall
                    addEntity(pos, new Wall(pos)); // assumes Wall also takes Position
                } else {
                    // 20% chance to add potion
                    double potionType = rand.nextDouble();
                    GameEntity potion;
                    if (potionType < 0.75) {
                        potion = new Potion(pos); // Position-based constructor
                    } else {
                        potion = new PowerPotion(pos); // Position-based constructor
                    }
                    addEntity(pos, potion);
                }
            }
        }

        Position playerPosition;
        do {
            int r = rand.nextInt(row);
            int c = rand.nextInt(col);
            playerPosition = new Position(r, c);
        } while (!isPositionFree(playerPosition));

        addEntity(playerPosition, player);

        revealNearby(playerPosition);

    }


    /**
     * Adds a GameEntity to a specific position.
     *
     */
    public boolean addEntity(Position pos, GameEntity entity) {
        if (pos == null || entity == null) return false;
        grid.putIfAbsent(pos,new ArrayList<>());
        grid.get(pos).add(entity);
        entity.setPosition(pos);
        return true;
    }

    /**
     * Removes a GameEntity from its position.
     */
    public boolean removeEntity(GameEntity entity) {
        if (entity == null || entity.getPosition() == null) return false;
        Position pos = entity.getPosition();
        List <GameEntity> entities = grid.get(pos);
        if (entities != null && entities.remove(entity)) {
            if (entities.isEmpty()) {
                grid.remove(pos);
            }
            return true;
        }
        return false;
    }


    /**
     * gets a list of all the entities on a specific position on them map
     */
    public List <GameEntity> getEntities(Position pos) {
        return grid.getOrDefault(pos, new ArrayList<>());
    }


    /**
     * checks if the specific position is blocked
     */
    public boolean isPositionFree(Position pos) {
        List <GameEntity> entities = grid.get(pos);
        if (entities == null) return true;
        return false;
    }


    /**
     * Reveals to the player everything from a manhattan distance of 2
     */
    public boolean revealNearby(Position pos) {

        if (pos == null) return false;

        for (Map.Entry<Position, List<GameEntity>> entry : grid.entrySet()) {
            if (pos.distanceTo(entry.getKey()) <= 2) {
                for (GameEntity entity : entry.getValue()) {
                    entity.setVisible(true);
                }
            }
        }
        return true;
    }


    /**
     * Moves a character to a position if possible
     */
    public boolean moveEntity(GameEntity entity, String direction){
        if (entity == null || direction == null || entity.getPosition()== null){
            return false;
        }
        if (!(entity instanceof PlayerCharacter)){
            return false;
        }
        Position current = entity.getPosition();
        Position next = switch (direction.toLowerCase()) {
            case "up" -> new Position(current.getRow() - 1, current.getCol());
            case "down" -> new Position(current.getRow() + 1, current.getCol());
            case "left" -> new Position(current.getRow(), current.getCol() - 1);
            case "right" -> new Position(current.getRow(), current.getCol() + 1);
            default -> null;
        };


        if (next == null) {
            System.out.println("Invalid direction: " + direction);
            return false;
        }


        if (next.getRow() < 0 || next.getRow() >= row
                || next.getCol() < 0 || next.getCol() >= col) {
            System.out.println("Cannot move " + direction + ": outside map bounds!");
            return false;
        }


        if (!isPositionFree(next)) {
            System.out.println("That space is blocked!");
            return false;
        }
        removeEntity(entity);

        return addEntity(next, entity);
    }

    /**
     * represents the map as a string
     */
    public String toString() {
        return "GameMap{" +
                "grid=" + grid +
                '}';
    }




    // Optional helper to safely check if an entity is visible
    private boolean isEntityVisible(GameEntity entity) {
        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getVisible");
            return (boolean) method.invoke(entity);
        } catch (Exception e) {
            return false;
        }
    }

    public void displayMap() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Position pos = new Position(r, c);
                List<GameEntity> entities = grid.get(pos);

                if (entities != null) {
                    boolean foundVisible = false;
                    for (GameEntity entity : entities) {
                        if (entity != null && isEntityVisible(entity)) {
                            System.out.print(entity.getDisplaySymbol() + " ");
                            foundVisible = true;
                            break; // show only one symbol per cell
                        }
                    }
                    if (!foundVisible) {
                        System.out.print(". ");
                    }
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }


    public List<GameEntity> getAllEntities() {
        return grid.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

}
