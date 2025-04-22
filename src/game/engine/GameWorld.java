package game.engine;
import game.characters.*;
import game.combat.CombatSystem;
import game.combat.BattleResult;
import game.core.GameEntity;
import game.items.GameItem;
import game.items.Potion;
import game.items.Treasure;
import game.map.GameMap;
import java.util.*;


/**
 * Represents the entire game state, including all players, enemies, items, and the map.
 *
 * <p>
 * The {@code GameWorld} class serves as a container for all active entities in the game
 * and provides access to the current state of the game map. It is primarily used by the
 * game engine to control interactions, combat, and movement.
 */
public class GameWorld {

    private static GameWorld instance;
    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;
    private List<BattleResult> battleResults = new ArrayList<>();
    /**
     * constructs the game world, according to the parameters of players, enemies, items, and the map
     */
    private GameWorld(List<PlayerCharacter> players, List<Enemy> enemies, List<GameItem> items, GameMap map) {
        this.players = players;
        this.enemies = enemies;
        this.items = items;
        this.map = map;
    }


    /**
     * Initializes the GameWorld with given parameters (only once).
     */
    public static GameWorld getInstance(List<PlayerCharacter> players, List<Enemy> enemies, List<GameItem> items, GameMap map) {
        if (instance == null) {
            instance = new GameWorld(players, enemies, items, map);
        }
        return instance;

    }


    /**
     * getter for the players list
     */
    public List<PlayerCharacter> getPlayers() {
        return players;
    }

    /**
     * getter for the enemies list
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * getter for the items list
     */
    public List<GameItem> getItems() {
        return items;
    }

    /**
     * getter for the game map
     */
    public GameMap getMap() {
        return map;
    }


    /**
     * Adds a new item to the game world.
     */
    public void addItem(GameItem item) {
        items.add(item);
    }

    /**
     * Removes an item from the game world.
     */
    public void removeItem(GameItem item) {
        items.remove(item);
    }

    /**
     * adds a player to the game world
     */
    public void addPlayer(PlayerCharacter player) {
        players.add(player);
    }

    /**
     * removes a player from the game world
     */
    public void removePlayer(PlayerCharacter player) {
        players.remove(player);
    }

    /**
     * adds an enemy to the game world
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * removes an enemy from the game world
     */
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * represents the game world to a string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Game World Summary ===\n");
        sb.append("Players: ").append(players.size()).append("\n");
        sb.append("Enemies: ").append(enemies.size()).append("\n");
        sb.append("Items: ").append(items.size()).append("\n");

        if (map != null) {
            sb.append("Map Info: ").append(map.toString()).append("\n");
        } else {
            sb.append("Map: Not initialized\n");
        }
        return sb.toString();
    }

    /**
     * the players turn during a game
     */
    public boolean turn(Scanner scanner) {

        PlayerCharacter player = players.get(0);
        map.revealNearby(player.getPosition());
        map.displayMap();
        boolean combatOccurred = false;

        List<Enemy> enemiesToRemove = new ArrayList<>();
        List <GameItem> itemsToRemove = new ArrayList<>();


        for (Enemy enemy : enemies) {
            if (!enemy.getVisible()) continue;
            combatOccurred = true;
            System.out.println(" ");
            System.out.println( enemy.enemyDiscription() + " in range, commencing combat!");
            BattleResult result = CombatSystem.resolveCombat(player, enemy);
            battleResults.add(result);

            if (player.isDead()) {
                System.out.println("You have died. Game Over.");
                return false;
            }
            if (enemy.isDead()) {
                System.out.println(enemy.enemyDiscription() + " has been defeated!");
                Treasure loot = enemy.defeat();
                loot.setVisible(true);
                map.removeEntity(enemy);
                map.addEntity(enemy.getPosition(), loot);
                items.add(loot);
                enemiesToRemove.add(enemy);
            }

        }

        if (combatOccurred) { // prevents printing the map twice when you do a non combat action
            map.revealNearby(player.getPosition());
            map.displayMap();
        }

        enemies.removeAll(enemiesToRemove);


        System.out.println("=== Your Turn ===");
        System.out.println("Choose : 1-[move] 2-[use potion] 3-[loot] 4-[show stats] 5-[exit]");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "1" -> {
                System.out.println("Direction? [up][down][left][right]");
                String direction = scanner.nextLine().trim().toLowerCase();
                instance.getMap().moveEntity(player, direction);

            }

            case "2" -> {
                if (player.getInventory().getItems().isEmpty()) {
                    System.out.println("You don't have any potions in your inventory!");
                    break;
                }

                System.out.println("Which kind of potion do you want to use? | 1 = Healing Potion | 2 = Power potion");
                int selectP = scanner.nextInt();
                scanner.nextLine();

                switch (selectP) {
                    case 1 -> {
                        for (GameItem item1 : player.getInventory().getItems()) {
                            if (item1.isHealingPotion()) {
                                player.usePotion();
                                ((Potion) item1).setIsPotionUsed(true); // this down casting is safe because of the isHealingPotion method, this only happens when the item is a healing potion
                                System.out.println("Used health Potion!");
                                System.out.println("Your new health is: " + player.getHealth());
                                break;
                            }
                        }
                    }
                    case 2 -> {
                        for (GameItem item2 : player.getInventory().getItems()) {
                            if (item2.isPowerPotion()) {
                                player.UsePowerPotion();
                                ((Potion) item2).setIsPotionUsed(true); // this down casting is safe because of the isPowerPotion method, this only happens when the item is a power potion
                                System.out.println("Used power potion!");
                                System.out.println("Your new power is: " + player.getPower());
                                break;
                            }
                        }
                    }

                    default -> System.out.println("Invalid choice");

                }

            }
            case "3" -> {
                for (GameItem item : items) {
                    if (item.getVisible()) {
                        if (player.getPosition().distanceTo(item.getPosition()) == 1) {
                            item.pickUp(player);
                            map.removeEntity(item);
                            itemsToRemove.add(item);
                        }
                    }
                }

                items.removeAll(itemsToRemove);
            }

            case "4" -> {
                System.out.println("Player: " + player.getName());
                System.out.println("Health: " + player.getHealth());
                System.out.println("Power: " + player.getPower());
                System.out.println("Treasure Points: " + player.getTreasurePoints());
                System.out.println("Inventory: " + player.getInventory().getItems().size() + " item(s)");
            }

            case "5" -> {
                System.out.println("Exiting game........");
                System.out.println("final stats: ");
                System.out.println("Health: " + player.getHealth());
                System.out.println("Power: " + player.getPower());
                System.out.println("Treasure Points: " + player.getTreasurePoints());


                return  false;
            }

            default -> System.out.println("Unknown command.");

        }



        return true;


    }


    /**
     * method for starting the game in the main method
     */
    public static void startGame () {
        Scanner scanner = new Scanner(System.in);

        System.out.println("------------Welcome to Dungeons and Dragons!------------");
        System.out.println("Enter your name: ");

        String name = scanner.nextLine().trim();

        PlayerCharacter player = null;

        System.out.println("Choose your character class:");
        System.out.println("1. Warrior");
        System.out.println("2. Mage");
        System.out.println("3. Archer");


        int choice = -1;
        while (choice < 1 || choice > 3) {
            System.out.print("Enter 1-3: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                // invalid input, retry
            }
        }

        switch (choice) {
            case 1 -> player = new Warrior(name);
            case 2 -> player = new Mage(name);
            case 3 -> player = new Archer(name);
        }


        List<PlayerCharacter> players = new ArrayList<>();
        players.add(player);


        System.out.println("How many rows do you want the map to have?");
        int rows = Integer.parseInt(scanner.nextLine());
        System.out.println("How many cols do you want the map to have?");
        int cols = Integer.parseInt(scanner.nextLine());

        GameMap map = new GameMap(rows, cols, player);

        List<Enemy> enemies = new ArrayList<>();
        List<GameItem> items = new ArrayList<>();
        for (GameEntity e : map.getAllEntities()) {
            if (e instanceof Enemy)     enemies.add((Enemy)e);
            if (e instanceof GameItem)  items.add((GameItem)e);
        }

        GameWorld.getInstance(players, enemies, items, map);

        GameWorld world = instance;

        while (world.turn(scanner)) {
            // The turn method handles game progression
        }

        System.out.println("\n=== Battle Summary Table ===");
        world.printBattleTable();
        System.out.println("Thank you for playing!");

        scanner.close();

    }

    /**
     * Prints a formatted table of all battles recorded during this turn.
     *
     * <p>This method builds and displays a header row followed by one line per
     * BattleResult in the `battleResults` list.  Each column has a fixed width,
     * ensuring that the output is neatly aligned in a console.
     *
     * Columns:
     * <ul>
     *   <li>Enemy:    the name of the enemy fought</li>
     *   <li>Rounds:   how many rounds the battle lasted</li>
     *   <li>Player Dmg: total damage dealt by the player</li>
     *   <li>Enemy Dmg: total damage dealt by the enemy</li>
     * </ul>
     */
    private void printBattleTable() {
        if (battleResults.isEmpty()) {
            return;  // no battles to report
        }

        // define column widths
        int wEnemy  = 10;
        int wRounds =  6;
        int wYou    = 13;
        int wThem   = 13;

        // build and print header
        String header =
                padRight("Enemy",    wEnemy)  + "|"
                        + padRight("Rounds",   wRounds) + "|"
                        + padRight("Player  Dmg", wYou)    + "|"
                        + padRight("Enemy Dmg", wThem);
        System.out.println();
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        // print each battle result in its own row
        for (BattleResult r : battleResults) {
            String line =
                    padRight(r.getEnemyName(),          wEnemy)  + "|"
                            + padRight(String.valueOf(r.getRounds()),      wRounds) + "|"
                            + padRight(String.valueOf(r.getDmgByPlayer()), wYou)    + "|"
                            + padRight(String.valueOf(r.getDmgByEnemy()),  wThem);
            System.out.println(line);
        }
    }

    /**
     * Pads or truncates a string to exactly the specified width.
     *
     * <p>If the input string is longer than `width`, it will be truncated
     * to `width` characters.  If it is shorter, spaces will be appended
     * on the right until the total length equals `width`.
     *
     * @param s     the original string to format
     * @param width the desired fixed width of the returned string
     * @return      a string whose length is exactly `width`
     */
    private static String padRight(String s, int width) {
        if (s.length() >= width) {
            // truncate any excess characters
            return s.substring(0, width);
        }
        // append spaces to reach the desired width
        return s + " ".repeat(width - s.length());
    }
}
