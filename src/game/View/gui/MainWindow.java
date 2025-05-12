package game.View.gui;

import game.Model.characters.Warrior;
import game.Model.characters.Mage;
import game.Model.characters.Archer;
import game.Model.characters.PlayerCharacter;
import game.Util.SoundPlayer;

import javax.swing.*;

/**
 * Entry point for the D&D GUI application. Prompts the user for
 * player name, class, and map size, then displays the main window.
 */
public class MainWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SoundPlayer.play("background_game_sound.mp3");
            // 1) Ask for player name
            String name = JOptionPane.showInputDialog(
                    null,
                    "Enter your character's name:",
                    "Dungeons & Dragons",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (name == null) {
                // Cancel pressed: exit immediately
                System.exit(0);
            }
            if (name.isBlank()) {
                name = "Hero";
            }

            // 2) Class selection
            String[] options = {"Warrior", "Mage", "Archer"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Choose your character class:",
                    "Dungeons & Dragons",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (choice < 0) {
                // dialog closed or Cancel: exit
                System.exit(0);
            }

            PlayerCharacter player;
            switch (choice) {
                case 1 -> player = new Mage(name);
                case 2 -> player = new Archer(name);
                default -> player = new Warrior(name);
            }

            // 3) Map dimensions (minimum 10)
            int rows = 10;
            int cols = 10;
            // rows
            String r = JOptionPane.showInputDialog(
                    null,
                    "Enter number of rows (>=10):",
                    "10"
            );
            if (r == null) {
                System.exit(0);
            }
            try {
                rows = Math.max(10, Integer.parseInt(r));
            } catch (NumberFormatException ignored) {
            }
            // cols
            String c = JOptionPane.showInputDialog(
                    null,
                    "Enter number of columns (>=10):",
                    "10"
            );
            if (c == null) {
                System.exit(0);
            }
            try {
                cols = Math.max(10, Integer.parseInt(c));
            } catch (NumberFormatException ignored) {
            }

            // build and show main frame
            JFrame frame = new JFrame("Dungeons & Dragons");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MainPanel(rows, cols, player));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
