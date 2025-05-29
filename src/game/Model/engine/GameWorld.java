package game.Model.engine;
import game.Controller.GameObserver;
import game.Model.characters.Enemy;
import game.Model.characters.PlayerCharacter;
import game.Model.combat.BattleResult;
import game.Model.combat.CombatSystem;
import game.Model.items.GameItem;
import game.Model.items.Potion;
import game.Model.items.Treasure;
import game.Model.map.GameMap;
import game.Model.map.Position;
import game.Util.SoundPlayer;
import game.Model.characters.Goblin;
import game.Model.characters.Orc;
import game.Model.characters.Dragon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Represents the entire game state, including all players, enemies, items, and the map.
 */
public class GameWorld {
    private static GameWorld instance;

    private final List<PlayerCharacter> players;
    private final List<Enemy>           enemies;
    private final List<GameItem>        items;
    private  GameMap               map;

    private final List<BattleResult>    battleResults = new ArrayList<>();
    private final List<GameObserver>    observers     = new CopyOnWriteArrayList<>();

    public enum Action { COMBAT, PICKUP, MOVE }
    private Position lastActionPos;
    private Action   lastAction;

    private final ScheduledExecutorService ses;
    private  AtomicBoolean isRunning;
    private final Random random = new Random();

    private ReentrantLock worldLock = new ReentrantLock(true);


    private GameWorld(List<PlayerCharacter> players,
                      List<Enemy>           enemies,
                      List<GameItem>        items) {
        this.players = players;
        this.enemies = enemies;
        this.items   = items;
        this.map = null;
        this.isRunning = new AtomicBoolean(false);
        this.ses = Executors.newScheduledThreadPool(Math.min(enemies.size(), Runtime.getRuntime().availableProcessors()));

    }



    public static GameWorld getInstance(List<PlayerCharacter> players,
                                        List<Enemy>           enemies,
                                        List<GameItem>        items) {
        if (instance == null) {
            instance = new GameWorld(players, enemies, items );
        }
        return instance;
    }


    public static GameWorld getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "GameWorld not initialized; call getInstance(players, enemies, items, map) first."
            );
        }
        return instance;
    }
    public void setMap(GameMap map) {
                if (this.map != null) throw new IllegalStateException("Map already set");
                this.map = map;
            }


    public void startGame() {
        isRunning.set(true);
        for (Enemy e : enemies) {
            scheduleEnemy(e, 500 + random.nextInt(1001));
        }
    }

    private void scheduleEnemy(Enemy e, long delayMs) {
        ses.schedule(() -> {
            if (!isRunning.get()) return;

            try {
                e.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            scheduleEnemy(e, 500 + random.nextInt(1001));
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    public void stopGame() {
        isRunning.set(false);
        ses.shutdown();
        try {
            if (!ses.awaitTermination(2, TimeUnit.SECONDS)) {
                ses.shutdownNow();
            }
        } catch (InterruptedException ex) {
            ses.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }


    public List<PlayerCharacter> getPlayers()     { return players; }
    public List<Enemy>           getEnemies()     { return enemies; }
    public List<GameItem>        getItems()       { return items; }
    public GameMap               getMap()         { return map; }
    public List<BattleResult>    getBattleResults() {
        return Collections.unmodifiableList(battleResults);
    }
    public Action getLastAction()      { return lastAction; }
    public Position getLastActionPos() { return lastActionPos; }


    public void registerObserver(GameObserver o) {
        observers.add(o);
    }
    public void unregisterObserver(GameObserver o) {
        observers.remove(o);
    }
    public void notifyObservers() {
        observers.forEach(GameObserver::onModelChanged);
    }


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

        lastActionPos = item.getPosition();
        lastAction    = Action.PICKUP;
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
            lastAction    = Action.PICKUP;
            notifyObservers();
        } else {
            System.out.println("Can't use " + item.getDisplaySymbol());
        }
    }

    public void attack(Enemy enemy) {
        if (!worldLock.tryLock()) return;
        try {
        PlayerCharacter player = players.get(0);


        switch (player.getClass().getSimpleName()) {
            case "Warrior" -> SoundPlayer.play("warrior_attack.wav");
            case "Mage"    -> SoundPlayer.play("mage_attack.wav");
            case "Archer"  -> SoundPlayer.play("archer_attack.wav");
        }


        BattleResult result = CombatSystem.resolveCombat(player, enemy);
        battleResults.add(result);

        lastActionPos = enemy.getPosition();
        lastAction    = Action.COMBAT;

        if (!enemy.isDead()) {
            if (enemy instanceof Goblin)   SoundPlayer.play("goblin_attack.wav");
            else if (enemy instanceof Orc) SoundPlayer.play("org_attack.wav");
            else if (enemy instanceof Dragon) SoundPlayer.play("dragon_attack.wav");
        }

        if (player.isDead()) {
            System.out.println("Game Over! " + player.getName() + " was defeated.");
            SoundPlayer.play("game_over.wav");
            notifyObservers();
            return;
        }


        if (enemy.isDead()) {
            System.out.println(enemy.enemyDiscription() + " defeated!");
            SoundPlayer.play("enemy_die.wav");
            Treasure loot = enemy.defeat();
            loot.setVisible(true);
            map.removeEntity(enemy);
            enemies.remove(enemy);
            map.addEntity(enemy.getPosition(), loot);
            items.add(loot);
        }

        map.revealNearby(player.getPosition());
        notifyObservers();
    }
        finally {
            worldLock.unlock();
        }
    }

    /**
     * Attempts to move the first player to `to`. Returns true if moved.
     */
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



    @Override
    public String toString() {
        var sb = new StringBuilder("=== Game World Summary ===\n");
        sb.append("Players: ").append(players.size()).append("\n");
        sb.append("Enemies: ").append(enemies.size()).append("\n");
        sb.append("Items:   ").append(items.size()).append("\n");
        sb.append("Map:     ").append(map).append("\n");
        return sb.toString();
    }
}
