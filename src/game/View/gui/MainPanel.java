package game.View.gui;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import game.Controller.GameObserver;
import game.Controller.GameController;
import game.Model.characters.PlayerCharacter;
import game.Model.characters.Enemy;
import game.Model.engine.GameWorld;
import game.Model.items.GameItem;
import game.Model.map.GameMap;

public class MainPanel extends JPanel {

    private final JLabel magicWaveLabel = new JLabel("", SwingConstants.CENTER);

    public MainPanel(int rows, int cols, PlayerCharacter player) {

        setLayout(new BorderLayout());


        List<PlayerCharacter> players = List.of(player);
        List<Enemy> enemies = new ArrayList<>();
        List<GameItem> items = new ArrayList<>();


        GameWorld world = GameWorld.getInstance(players, enemies, items);
        GameMap map = new GameMap(rows, cols, player, world);
        world.setMap(map);


        magicWaveLabel.setFont(magicWaveLabel.getFont().deriveFont(Font.BOLD, 16f));
        magicWaveLabel.setForeground(Color.RED);
        magicWaveLabel.setOpaque(true);
        magicWaveLabel.setBackground(new Color(0, 0, 0, 100));
        magicWaveLabel.setVisible(false);
        add(magicWaveLabel, BorderLayout.NORTH);


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


                if (world.wasGameEvent()) {
                    showMagicWaveMessage();
                }
            }
        });

        GameController controller = new GameController(world);
        MapPanel mapPanel = new MapPanel(world, controller);
        StatusPanel statusPanel = new StatusPanel(world);
        BattleLogPanel logPanel = new BattleLogPanel(world);
        InventoryPanel inventoryPanel = new InventoryPanel(world);

        add(mapPanel,       BorderLayout.CENTER);
        add(statusPanel,    BorderLayout.EAST);
        add(logPanel,       BorderLayout.SOUTH);
        add(inventoryPanel, BorderLayout.WEST);

        world.startGame();

        SwingUtilities.invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame frame) {
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        world.stopGame();
                    }
                });
            }
        });
    }


    private void showMagicWaveMessage() {
        magicWaveLabel.setText("🔮 Magic Wave!");
        magicWaveLabel.setVisible(true);

        magicWaveLabel.revalidate();
        magicWaveLabel.repaint();

        new Timer(2500, evt -> {
            magicWaveLabel.setVisible(false);
            magicWaveLabel.revalidate();
            magicWaveLabel.repaint();
        }) {{
            setRepeats(false);
            start();
        }};
    }
}
