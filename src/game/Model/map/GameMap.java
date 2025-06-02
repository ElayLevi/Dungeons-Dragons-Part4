package game.Model.map;
import game.Model.characters.*;
import game.Model.core.GameEntity;
import game.Model.engine.GameWorld;
import game.Model.items.Potion;
import game.Model.items.PowerPotion;
import game.Model.items.Wall;
import game.Util.GameLogger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
    private ReentrantLock mapLock = new ReentrantLock(true);


    /**
     * Constructs an empty GameMap.
     */
    public GameMap(int row, int col, PlayerCharacter player, GameWorld world) {
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
                    Enemy enemy = switch (rand.nextInt(3)) {
                        case 0 -> new Goblin(world);
                        case 1 -> new Orc(world);
                        default -> new Dragon(world);
                    };

                    enemy.setPosition(pos);
                    world.getEnemies().add(enemy);
                    addEntity(pos,enemy);

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

    private boolean tryLockMap(long timeoutMS) {
        try {
            return mapLock.tryLock(timeoutMS, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }


    private void unlockMap() {
        if (mapLock.isHeldByCurrentThread()) {
            mapLock.unlock();
        }
    }


    /**
     * Adds a GameEntity to a specific position.
     *
     */
    public boolean addEntity(Position pos, GameEntity entity) {
        if (!tryLockMap(200))
            return false;
        try {
            if (pos == null || entity == null) return false;
            grid.putIfAbsent(pos, new ArrayList<>());
            grid.get(pos).add(entity);
            entity.setPosition(pos);
            return true;
        }
        finally {
            unlockMap();
        }
    }

    /**
     * Removes a GameEntity from its position.
     */
    public boolean removeEntity(GameEntity entity) {
        if (!tryLockMap(200))
            return false;
        try {
            if (entity == null || entity.getPosition() == null) return false;
            Position pos = entity.getPosition();
            List<GameEntity> entities = grid.get(pos);
            if (entities != null && entities.remove(entity)) {
                if (entities.isEmpty()) {
                    grid.remove(pos);
                }
                return true;
            }
            return false;
        }
        finally {
            unlockMap();
        }
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
                    if (!entity.getVisible()) {
                        GameLogger.getInstance().log(" Revealed " + entity.getDisplaySymbol() + " at " + entry.getKey());
                        entity.setVisible(true);
                    }
                }
            }
        }
        return true;
    }


    /**
     * Moves a character to a position if possible
     */
    public boolean moveEntity(GameEntity entity, String direction) {
        if (!tryLockMap(200))
            return false;
        try {
            if (entity == null || direction == null || entity.getPosition() == null) {
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
                GameLogger.getInstance().log(entity.getDisplaySymbol() + " tried to move in invalid direction " + direction + " from " + current);
                return false;
            }

            if (next.getRow() < 0 || next.getRow() >= row ||
                    next.getCol() < 0 || next.getCol() >= col) {
                System.out.println("Cannot move " + direction + ": outside map bounds!");
                GameLogger.getInstance().log(entity.getDisplaySymbol() + " tried to move " + direction + " from " + current + " but hit map boundary ");
                return false;
            }

            for (GameEntity e : getEntities(next)) {
                if (e instanceof Wall || e instanceof Enemy) {
                    System.out.println("That space is blocked!");
                    GameLogger.getInstance().log(entity.getDisplaySymbol() + " tried to move " + direction + " from " + current + " but was blocked by " + e.getDisplaySymbol() + " at " + next);
                    return false;
                }
            }

            removeEntity(entity);

            boolean added = addEntity(next, entity);
            if (added) {
                GameLogger.getInstance().log(entity.getDisplaySymbol() + " moved from " + current + " to " + next);
            }

            return added;
        }
        finally {
            unlockMap();
        }
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

    public int getNumRows() {
        return row;
    }

    public int getNumCols() {
        return col;
    }
}
