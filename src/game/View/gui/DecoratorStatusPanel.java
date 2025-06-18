package game.View.gui;
import game.Model.decorator.DecoratorManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays all active decorators in the game.
 * Updates every second to show remaining time.
 */
public class DecoratorStatusPanel extends JPanel {
    private JTextArea statusArea;

    public DecoratorStatusPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Active Power-ups"));
        setPreferredSize(new Dimension(200, 150));

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));

        add(new JScrollPane(statusArea), BorderLayout.CENTER);

        // Update every second
        Timer updateTimer = new Timer(1000, e -> updateStatus());
        updateTimer.start();
    }

    private void updateStatus() {
        DecoratorManager manager = DecoratorManager.getInstance();
        Map<String, List<String>> decoratorInfo = manager.getActiveDecoratorInfo();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : decoratorInfo.entrySet()) {
            sb.append(entry.getKey()).append(":\n");
            for (String decorator : entry.getValue()) {
                sb.append("  • ").append(decorator).append("\n");
            }
            sb.append("\n");
        }

        statusArea.setText(sb.toString());
    }
}