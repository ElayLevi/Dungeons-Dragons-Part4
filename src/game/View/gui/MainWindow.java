package game.View.gui;

import game.Model.characters.PlayerCharacter;
import game.Model.characters.Enemy;
import game.Model.items.GameItem;
import game.Model.engine.GameWorld;
import game.Model.map.GameMap;
import game.Util.SoundPlayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1) Character creation
            CharacterCreationDialog dialog = new CharacterCreationDialog(null);
            PlayerCharacter player = dialog.showDialog();
            if (player == null) System.exit(0);

            // 2) Background music
            SoundPlayer.play("background_game_sound.wav");

            // 3) Map size input
            int rows = 10, cols = 10;
            try {
                String rs = JOptionPane.showInputDialog(null, "Enter number of rows (>=10):", "10");
                if (rs == null) System.exit(0);
                rows = Math.max(10, Integer.parseInt(rs));

                String cs = JOptionPane.showInputDialog(null, "Enter number of columns (>=10):", "10");
                if (cs == null) System.exit(0);
                cols = Math.max(10, Integer.parseInt(cs));
            } catch (NumberFormatException ignored) { }

            // 4) Initialize GameWorld
            List<PlayerCharacter> players = List.of(player);
            List<Enemy> enemies = new ArrayList<>();
            List<GameItem> items = new ArrayList<>();

            GameWorld world = GameWorld.getInstance();
            world.initialize(players, enemies, items);

            GameMap map = new GameMap(rows, cols, player, world);
            world.setMap(map);

            // ─────────────────────────────────────────────────────────────────
            //  Find your **chosen** top-level 'saves/' directory once (no src/!)
            File projectRoot = new File(System.getProperty("user.dir"));
            File saveDir = new File(projectRoot, "saves");
            if (!saveDir.isDirectory()) {
                JOptionPane.showMessageDialog(null,
                        "Cannot find your saves/ folder:\n" + saveDir.getAbsolutePath(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // 5) Build main window + menu
            JFrame frame = new JFrame("Dungeons & Dragons");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();
            JMenu    gameMenu = new JMenu("Game");
            menuBar.add(gameMenu);



            // — Save to File…
            JMenuItem miSave = new JMenuItem("Save to File…");
            miSave.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser(saveDir);
                chooser.setDialogTitle("Save Game");
                chooser.setFileFilter(new FileNameExtensionFilter("Game Saves (*.sav)", "sav"));

                if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File picked = chooser.getSelectedFile();
                String name = picked.getName();
                if (!name.toLowerCase().endsWith(".sav")) {
                    name += ".sav";
                }
                File target = new File(saveDir, name);

                try {
                    world.saveToFile(target.getAbsolutePath());
                    JOptionPane.showMessageDialog(frame,
                            "Game saved to:\n" + target.getName(),
                            "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to save game:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            gameMenu.add(miSave);

            // — Load from File…
            JMenuItem miLoad = new JMenuItem("Load from File…");
            miLoad.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser(saveDir);
                chooser.setDialogTitle("Load Game");
                chooser.setFileFilter(new FileNameExtensionFilter("Game Saves (*.sav)", "sav"));

                if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File picked = chooser.getSelectedFile();
                try {
                    world.loadFromFile(picked.getAbsolutePath());
                    JOptionPane.showMessageDialog(frame,
                            "Game loaded from:\n" + picked.getName(),
                            "Loaded", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to load game:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            gameMenu.add(miLoad);

            frame.setJMenuBar(menuBar);

            // 6) Finish UI
            MainPanel mp = new MainPanel(rows, cols, player);
            frame.setContentPane(mp);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 7) Cleanup on close
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    world.stopGame();
                }
            });

            // 8) Start the game loop
            world.startGame();
        });
    }
}