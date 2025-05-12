package game.View.gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import game.Controller.GameObserver;
import game.Model.items.Potion;
import game.Model.items.PowerPotion;
import game.View.gui.BattleLogPanel;
import game.Model.engine.GameWorld;
import game.Model.items.GameItem;
import game.Model.map.GameMap;
import game.Model.map.Position;
import game.Model.characters.*;
import game.Controller.GameController;

public class MainPanel extends JPanel {
    public MainPanel(int rows, int cols, PlayerCharacter player) {
        super(new BorderLayout());

        GameMap map = new GameMap(rows, cols, player);

        List<PlayerCharacter> players = List.of(player);
        List<Enemy> enemies = new ArrayList<>();
        List<GameItem> items = new ArrayList<>();


        GameWorld world = GameWorld.getInstance(players, enemies, items, map);
        world.registerObserver(new GameObserver() {
            @Override
            public void onModelChanged() {
                PlayerCharacter p = world.getPlayers().get(0);
                if (p.isDead()) {
                    JOptionPane.showMessageDialog(
                            SwingUtilities.getWindowAncestor(MainPanel.this),
                            "Game Over",
                            "Dungeons & Dragons",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    System.exit(0);
                }
            }
        });
        // 2. Controller
        GameController controller = new GameController(world);

        // 3. GUI
        MapPanel mapPanel = new MapPanel(world, controller);
        StatusPanel statusPanel = new StatusPanel(world);
        BattleLogPanel logPanel = new BattleLogPanel(world);
        InventoryPanel inventoryPanel = new InventoryPanel(world);

        setLayout(new BorderLayout());
        add(mapPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.EAST);
        add(logPanel,    BorderLayout.SOUTH);
        add(inventoryPanel, BorderLayout.WEST);
    }


}

