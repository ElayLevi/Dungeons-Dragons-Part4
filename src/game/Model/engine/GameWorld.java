package game.Model.engine;

import game.Controller.GameObserver;
import game.Model.Factory.EnemyFactory;
import game.Model.Factory.ItemFactory;
import game.Model.Factory.PlayerFactory;
import game.Model.characters.Enemy;
import game.Model.characters.PlayerCharacter;
import game.Model.combat.BattleResult;
import game.Model.combat.CombatSystem;
import game.Model.decorator.DecoratorManager;
import game.Model.items.GameItem;
import game.Model.items.Potion;
import game.Model.items.Treasure;
import game.Model.map.GameMap;
import game.Model.map.Position;
import game.Util.GameLogger;
import game.Util.SoundPlayer;
import game.Model.characters.Goblin;
import game.Model.characters.Orc;
import game.Model.characters.Dragon;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
import game.Model.memento.GameMemento;
import game.Model.memento.PlayerSnapshot;
import game.Model.memento.EnemySnapshot;
import game.Model.memento.ItemSnapshot;

/**
 * Represents the entire game state using Singleton pattern.
 * This ensures only one game board exists throughout the application.
 *

 */
public class GameWorld {
    // Volatile to ensure thread-safe singleton
    private static volatile GameWorld instance;
    private static final Object lock = new Object();

    // Thread pool for enemies - will be updated to meet assignment requirements
    private ExecutorService enemyThreadPool;
    private final int threadPoolSize;

    private boolean lastEvent = false;

    private List<PlayerCharacter> players;
    private PlayerCharacter player;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;

    private final List<BattleResult> battleResults = new ArrayList<>();
    private final List<GameObserver> observers = new CopyOnWriteArrayList<>();

    public enum Action {COMBAT, PICKUP, MOVE}

    private Position lastActionPos;
    private Action lastAction;

    private ScheduledExecutorService enemyScheduler;
    private ScheduledExecutorService worldEventScheduler;
    private AtomicBoolean isRunning;
    private final Random random = new Random();
    private ReentrantLock worldLock = new ReentrantLock(true);
    private final Deque<GameMemento> history = new ArrayDeque<>();
    /**
     * Private constructor for Singleton pattern
     */
    private GameWorld() {
        this.isRunning = new AtomicBoolean(false);
        this.threadPoolSize = calculateThreadPoolSize();
        initializeExecutors();
    }

    /**
     * Calculate thread pool size based on board size
     * 3% of board size, minimum 1, maximum 10
     */
    private int calculateThreadPoolSize() {
        // Default size if map not set yet
        int boardSize = 100; // Will be updated when map is set
        if (map != null) {
            boardSize = map.getNumRows() * map.getNumCols();
        }
        int poolSize = Math.max(1, (int)(boardSize * 0.03));
        return Math.min(poolSize, 10);
    }

    /**
     * Initialize all executor services
     */
    private void initializeExecutors() {
        // Fixed thread pool for enemies
        this.enemyThreadPool = Executors.newFixedThreadPool(threadPoolSize, r -> {
            Thread t = new Thread(r, "EnemyWorker-" + Thread.currentThread().getId());
            t.setDaemon(true);
            return t;
        });

        // Scheduler for enemy tasks
        this.enemyScheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "EnemyScheduler");
            t.setDaemon(true);
            return t;
        });

        // World event scheduler
        this.worldEventScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "WorldEventThread");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Get the singleton instance of GameWorld
     * Thread-safe implementation using double-checked locking
     *
     * @return The single instance of GameWorld
     */
    public static GameWorld getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameWorld();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize the game world with players, enemies, and items
     * This replaces the old getInstance with parameters
     *
     * @param players List of player characters
     * @param enemies List of enemies
     * @param items List of game items
     */
    public void initialize(List<PlayerCharacter> players, List<Enemy> enemies, List<GameItem> items) {
        if (this.players != null || this.enemies != null || this.items != null) {
            throw new IllegalStateException("GameWorld already initialized");
        }

        this.players = new CopyOnWriteArrayList<>(players);
        this.player  = this.players.get(0);
        this.enemies = new CopyOnWriteArrayList<>(enemies);
        this.items = new CopyOnWriteArrayList<>(items);
    }

    /**
     * Reset the singleton instance (useful for testing or restarting game)
     */
    public static void resetInstance() {
        synchronized (lock) {
            if (instance != null) {
                instance.stopGame();
                instance = null;
            }
        }
    }

    /**
     * Set the game map and recalculate thread pool size
     *
     * @param map The game map
     */
    public void setMap(GameMap map) {
        if (this.map != null) {
            throw new IllegalStateException("Map already set");
        }
        this.map = map;

        // Recalculate and update thread pool size based on actual map size
        int newPoolSize = calculateThreadPoolSize();
        if (newPoolSize != threadPoolSize && enemyThreadPool != null) {
            // Recreate thread pool with new size
            ExecutorService oldPool = enemyThreadPool;
            enemyThreadPool = Executors.newFixedThreadPool(newPoolSize, r -> {
                Thread t = new Thread(r, "EnemyWorker-" + Thread.currentThread().getId());
                t.setDaemon(true);
                return t;
            });
            oldPool.shutdown();
        }

        // Initialize enemies on the map
        initializeEnemies();
    }

    /**
     * Initialize enemies on the map using the factory pattern.
     * Creates initial enemies based on map size.
     */
    private void initializeEnemies() {
        int boardSize = map.getNumRows() * map.getNumCols();
        int initialEnemyCount = (int)(boardSize * 0.3); // 30% of positions might have enemies

        Random rand = new Random();
        int enemiesCreated = 0;

        for (int i = 0; i < initialEnemyCount; i++) {
            if (rand.nextDouble() < 0.7) { // 70% chance for each potential enemy position
                // Use factory to create enemy
                String[] enemyTypes = {"Goblin", "Orc", "Dragon"};
                String selectedType = enemyTypes[rand.nextInt(enemyTypes.length)];

                game.Model.Factory.EnemyFactory.createEnemyOfType(selectedType);
                enemiesCreated++;
            }
        }

        GameLogger.getInstance().log("Initialized " + enemiesCreated + " enemies on the map");
    }


    public void gameEvent() {
        lastEvent = true;

        if (random.nextBoolean()) {
            dmgMagicWave();
            GameLogger.getInstance().log("A magic wave occurs that damaged everyone");
        } else {
            pwrMagicWave();
            GameLogger.getInstance().log("A power wave occurs that raises everyone's power");
        }

        notifyObservers();
    }

    public void startGame() {
        SoundPlayer.play("background_game_sound.wav");
        GameLogger.getInstance().log("Game Started");
        isRunning.set(true);

        for (Enemy e : enemies) {
            scheduleEnemy(e, 500 + random.nextInt(1001));
        }

        worldEventScheduler.scheduleAtFixedRate(
                this::gameEvent,
                40, 40,
                TimeUnit.SECONDS
        );
    }

    public boolean wasGameEvent() {
        if (lastEvent) {
            lastEvent = false;
            return true;
        }
        return false;
    }

    private void scheduleEnemy(Enemy e, long delayMs) {
        enemyScheduler.schedule(() -> {
            if (!isRunning.get() || e.isDead()) return;

            // Submit enemy task to thread pool
            enemyThreadPool.submit(new EnemyTask(e));

            // Schedule next execution if enemy is still alive
            if (!e.isDead()) {
                long nextDelay = 500 + random.nextInt(1001);
                scheduleEnemy(e, nextDelay);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Schedules a new enemy to start running.
     * Called by EnemyTask when an enemy dies and needs replacement.
     *
     * @param enemy The new enemy to schedule
     */
    public void scheduleNewEnemy(Enemy enemy) {
        if (isRunning.get() && enemy != null) {
            long initialDelay = 500 + random.nextInt(1001);
            scheduleEnemy(enemy, initialDelay);
        }
    }



    public void stopGame() {
        isRunning.set(false);

        enemyScheduler.shutdownNow();
        worldEventScheduler.shutdownNow();

        enemyThreadPool.shutdown();
        try {
            if (!enemyThreadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                enemyThreadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            enemyThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        GameLogger.getInstance().log("Game Ended");
    }
    /**
     * Serialize the current world state to the given file.
     */
    public void saveToFile(String fullPath) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(fullPath))) {
            out.writeObject(this.saveState());
        }
    }

    /**
     * Load a previously saved memento from src/saves/<fileName> and restore it.
     */
    public void loadFromFile(String fullPath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(fullPath))) {
            GameMemento m = (GameMemento) in.readObject();
            this.restoreState(m);
        }
    }
    /**
     * Capture a full snapshot of the current world and push it onto the history stack.
     */

    public GameMemento saveState() {
        // 1) capture the (sole) player
        PlayerSnapshot playerSnap = new PlayerSnapshot(players.get(0));

        // 2) capture every _live_ enemy
        List<EnemySnapshot> enemySnaps = enemies.stream()
                .filter(e -> !e.isDead())
                .map(EnemySnapshot::new)
                .collect(Collectors.toList());

        // 3) capture every item
        List<ItemSnapshot> itemSnaps = items.stream()
                .map(ItemSnapshot::new)
                .collect(Collectors.toList());

        // 4) bundle up last action
        String lastAct = (this.lastAction == null)
                ? null
                : this.lastAction.name();
        Position lastActPos = (this.lastActionPos != null)
                ? new Position(this.lastActionPos)
                : null;

        return new GameMemento(
                playerSnap,
                enemySnaps,
                itemSnaps,
                lastAct,
                lastActPos
        );
    }



    public void restoreState(GameMemento m) {
        // A) clear out the old world completely
        map.clearAll();
        players.clear();
        enemies.clear();
        items.clear();

        // B) restore the player
        PlayerSnapshot ps = m.getPlayerState();
        PlayerCharacter p = PlayerFactory.instantiateByType(
                ps.getTypeName(),
                ps.getPlayerName()
        );

        p.setPosition(ps.getPosition());
        p.setHealth(ps.getHealth());
        p.setPower(ps.getPower());
        p.updateTreasurePoint(ps.getTreasurePoints());
        p.setEvasionChance(ps.getEvasionChance());

        // FIXED: Restore inventory items
        for (String itemType : ps.getInventoryItemTypes()) {
            GameItem item = ItemFactory.createItem(itemType);
            if (item != null) {
                p.addToInventory(item);
            }
        }

        players.add(p);
        map.addEntity(p.getPosition(), p);

        // C) restore each enemy exactly where it was saved
        for (EnemySnapshot es : m.getEnemyStates()) {
            Enemy e = EnemyFactory.instantiateByType(es.getTypeName());
            e.setPosition(es.getPosition());
            e.setHealth(es.getHealth());
            e.setPower(es.getPower());
            enemies.add(e);
            map.addEntity(e.getPosition(), e);
        }

        // D) restore each item
        for (ItemSnapshot is : m.getItemStates()) {
            GameItem it = ItemFactory.createItem(is.getTypeName());
            it.setPosition(is.getPosition());
            it.setVisible(is.isVisible());
            items.add(it);
            map.addEntity(it.getPosition(), it);
        }

        // E) restore the last‐action fields
        this.lastAction = (m.getLastAction() == null)
                ? null
                : Action.valueOf(m.getLastAction());
        this.lastActionPos = (m.getLastActionPos() != null)
                ? new Position(m.getLastActionPos())
                : null;
    }




    // All other methods remain the same...
    public boolean isRunning() { return isRunning.get(); }
    public List<PlayerCharacter> getPlayers() { return players; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<GameItem> getItems() { return items; }
    public GameMap getMap() { return map; }
    public List<BattleResult> getBattleResults() { return Collections.unmodifiableList(battleResults); }
    public Action getLastAction() { return lastAction; }
    public Position getLastActionPos() { return lastActionPos; }

    public void registerObserver(GameObserver o) { observers.add(o); }
    public void unregisterObserver(GameObserver o) { observers.remove(o); }
    public void notifyObservers() { observers.forEach(GameObserver::onModelChanged); }

    public boolean pickUpItem(GameItem item) {
        if (item == null) return false;

        map.removeEntity(item);
        items.remove(item);

        PlayerCharacter player = players.get(0);
        if (item instanceof Potion) {
            player.addToInventory(item);
        } else if (item instanceof Treasure t) {
            player.updateTreasurePoint(t.getValue());
            SoundPlayer.play("treasure-sound.wav");
        }

        GameLogger.getInstance().log(player.getName() + " picked up " + item.getDisplaySymbol() + " at " + item.getPosition());

        lastActionPos = item.getPosition();
        lastAction = Action.PICKUP;
        notifyObservers();
        return true;
    }

    public void useItem(GameItem item) {
        if (item == null) return;

        PlayerCharacter player = players.get(0);
        boolean used = false;

        if (item.isHealingPotion()) {
            used = player.usePotion();
            SoundPlayer.play("drink_potion.wav");
        } else if (item.isPowerPotion()) {
            used = player.UsePowerPotion();
            SoundPlayer.play("drink_potion.wav");
        }

        if (used) {
            map.revealNearby(player.getPosition());
            lastActionPos = player.getPosition();
            lastAction = Action.PICKUP;
            notifyObservers();

            GameLogger.getInstance().log(player.getName() + " used potion " + item.getDisplaySymbol() + " at " + player.getPosition());
        } else {
            System.out.println("Can't use " + item.getDisplaySymbol());
            GameLogger.getInstance().log(player.getName() + " failed to use " + item.getDisplaySymbol());
        }
    }

    public void attack(Enemy enemy) {
        if (!worldLock.tryLock()) return;
        try {
            PlayerCharacter player = players.get(0);

            GameLogger.getInstance().log(player.getName() + " attacked " + enemy.getDisplaySymbol() + " at " + enemy.getPosition());

            switch (player.getClass().getSimpleName()) {
                case "Warrior" -> SoundPlayer.play("warrior_attack.wav");
                case "Mage" -> SoundPlayer.play("mage_attack.wav");
                case "Archer" -> SoundPlayer.play("archer_attack.wav");
            }

            BattleResult result = CombatSystem.resolveCombat(player, enemy);
            battleResults.add(result);

            lastActionPos = enemy.getPosition();
            lastAction = Action.COMBAT;

            if (!enemy.isDead()) {
                if (enemy instanceof Goblin) SoundPlayer.play("goblin_attack.wav");
                else if (enemy instanceof Orc) SoundPlayer.play("org_attack.wav");
                else if (enemy instanceof Dragon) SoundPlayer.play("dragon_attack.wav");
            }

            if (enemy.isDead()) {
                GameLogger.getInstance().log(enemy.getDisplaySymbol() + " died after attack by " + player.getName());
            } else {
                GameLogger.getInstance().log(enemy.getDisplaySymbol() + " has " + enemy.getHealth() + " HP remaining ");
            }

            if (player.isDead()) {
                System.out.println("Game Over! " + player.getName() + " was defeated. ");
                SoundPlayer.play("game_over.wav");
                GameLogger.getInstance().log(player.getName() + " died during combat ");
                notifyObservers();
                return;
            }

            if (enemy.isDead()) {
                System.out.println(enemy.enemyDiscription() + " defeated!");
                SoundPlayer.play("enemy_die.wav");
                GameLogger.getInstance().log(enemy.getDisplaySymbol() + " was defeated by " + player.getName());
                Treasure loot = enemy.defeat();
                loot.setVisible(true);
                map.removeEntity(enemy);
                enemies.remove(enemy);
                map.addEntity(enemy.getPosition(), loot);
                items.add(loot);
                GameLogger.getInstance().log(" Loot " + loot.getDisplaySymbol() + " appeared at " + loot.getPosition());
            }

            map.revealNearby(player.getPosition());
            notifyObservers();
        } finally {
            worldLock.unlock();
        }
    }

    public boolean movePlayer(Position to) {
        if (!worldLock.tryLock()) {
            return false;
        }
        try {
            PlayerCharacter player = players.get(0);
            Position from = player.getPosition();

            String dir;
            if (to.getRow() == from.getRow() + 1 && to.getCol() == from.getCol()) dir = "down";
            else if (to.getRow() == from.getRow() - 1 && to.getCol() == from.getCol()) dir = "up";
            else if (to.getCol() == from.getCol() + 1 && to.getRow() == from.getRow()) dir = "right";
            else if (to.getCol() == from.getCol() - 1 && to.getRow() == from.getRow()) dir = "left";
            else return false;

            boolean moved = map.moveEntity(player, dir);
            if (moved) {
                GameLogger.getInstance().log(player.getName() + " moved from " + from + " to " + to);
                lastActionPos = to;
                lastAction = Action.MOVE;
                map.revealNearby(player.getPosition());
                SoundPlayer.play("footsteps.wav");
                notifyObservers();
            }
            return moved;

        } finally {
            worldLock.unlock();
        }
    }

    private void pwrMagicWave() {
        System.out.println("A power wave occurs and empowers everyone!");

        Random rand = new Random();
        int powerGiven = rand.nextInt(11) + 5;

        for (PlayerCharacter player : players) {
            if (!player.isDead()) {
                int playerPower = player.getPower() + powerGiven;
                player.setPower(playerPower);
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                int enemyPower = enemy.getPower() + powerGiven;
                enemy.setPower(enemyPower);
            }
        }
    }

    private void dmgMagicWave() {
        System.out.println("A magic wave occurs and damages everyone!");

        Random rand = new Random();
        int waveDMG = rand.nextInt(11) + 5; // between 5 - 15

        for (PlayerCharacter player : players) {
            if (!player.isDead()) {
                player.takeDamage(waveDMG);
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                enemy.takeDamage(waveDMG);
            }
        }
    }

    public void applyRandomEnemyDecorators() {
        DecoratorManager manager = DecoratorManager.getInstance();
        Random rand = new Random();

        for (Enemy enemy : enemies) {
            // 20% chance to get a decorator
            if (rand.nextDouble() < 0.2) {
                // Random duration between 30-60 seconds
                int duration = 30 + rand.nextInt(31);
                Enemy decorated = manager.applyRandomEnemyDecorator(enemy, duration);

                // Update enemy in list
                int index = enemies.indexOf(enemy);
                if (index >= 0) {
                    enemies.set(index, decorated);
                }
            }
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("=== Game World Summary ===\n");
        sb.append("Players: ").append(players != null ? players.size() : 0).append("\n");
        sb.append("Enemies: ").append(enemies != null ? enemies.size() : 0).append("\n");
        sb.append("Items:   ").append(items != null ? items.size() : 0).append("\n");
        sb.append("Map:     ").append(map).append("\n");
        sb.append("Thread Pool Size: ").append(threadPoolSize).append("\n");
        return sb.toString();
    }
}