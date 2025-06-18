package game.View.gui;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import game.Controller.GameObserver;
import game.Controller.GameController;
import game.Model.characters.PlayerCharacter;
import game.Model.characters.Enemy;
import game.Model.decorator.DecoratorManager;
import game.Model.engine.GameWorld;
import game.Model.items.GameItem;
import game.Model.map.GameMap;

public class MainPanel extends JPanel {

    private long lastPowerUpTime = 0;
    private static final long POWER_UP_COOLDOWN = 180000; // 3 minutes
    private final JLabel magicWaveLabel = new JLabel("", SwingConstants.CENTER);

    public MainPanel(int rows, int cols, PlayerCharacter player) {

        setLayout(new BorderLayout());

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(new DecoratorStatusPanel(), BorderLayout.NORTH);

        List<PlayerCharacter> players = List.of(player);
        List<Enemy> enemies = new ArrayList<>();
        List<GameItem> items = new ArrayList<>();


        GameWorld world = GameWorld.getInstance();
        GameMap map = world.getMap();

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

        setupDecoratorShortcuts();

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

    private void setupDecoratorShortcuts() {
        // F3 - Activate temporary power-up
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "activatePowerUp");
        getActionMap().put("activatePowerUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activateTemporaryPowerUp();
            }
        });
    }


    /**
     * Shows a temporary notification message to the user.
     *
     * @param message The message to display
     */
    private void showNotification(String message) {
        // Option 1: Use a temporary label (similar to your magic wave label)
        JLabel notificationLabel = new JLabel(message);
        notificationLabel.setFont(notificationLabel.getFont().deriveFont(Font.BOLD, 14f));
        notificationLabel.setForeground(Color.GREEN);
        notificationLabel.setOpaque(true);
        notificationLabel.setBackground(new Color(0, 0, 0, 180));
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add to the top of the panel temporarily
        add(notificationLabel, BorderLayout.NORTH);
        revalidate();
        repaint();

        // Remove after 2 seconds
        Timer timer = new Timer(2000, e -> {
            remove(notificationLabel);
            add(magicWaveLabel, BorderLayout.NORTH); // Restore the magic wave label
            revalidate();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Fix the activateTemporaryPowerUp method:
    private void activateTemporaryPowerUp() {
        long currentTime = System.currentTimeMillis();

        // Check cooldown
        if (currentTime - lastPowerUpTime < POWER_UP_COOLDOWN) {
            long remainingCooldown = (POWER_UP_COOLDOWN - (currentTime - lastPowerUpTime)) / 1000;
            showNotification("Power-up on cooldown: " + remainingCooldown + "s remaining");
            return;
        }

        GameWorld world = GameWorld.getInstance();
        if (world.getPlayers().isEmpty()) return;

        PlayerCharacter player = world.getPlayers().get(0);

        // Show selection dialog for single power-up
        String[] options = Arrays.stream(DecoratorManager.PlayerDecoratorType.values())
                .map(DecoratorManager.PlayerDecoratorType::getDisplayName)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a temporary power-up (15 seconds):",
                "Power-up Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected != null) {
            // Find the selected type
            DecoratorManager.PlayerDecoratorType selectedType = null;
            for (DecoratorManager.PlayerDecoratorType type : DecoratorManager.PlayerDecoratorType.values()) {
                if (type.getDisplayName().equals(selected)) {
                    selectedType = type;
                    break;
                }
            }

            if (selectedType != null) {
                DecoratorManager manager = DecoratorManager.getInstance();
                // Apply decorator - it returns the original player since we track decorators separately
                manager.applyPlayerDecorator(player, selectedType, 15);

                lastPowerUpTime = currentTime;
                showNotification("Power-up activated: " + selected + " (15s)");
            }
        }
    }

}