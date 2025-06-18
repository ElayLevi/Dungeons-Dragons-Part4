package game.View.gui;

import game.Model.characters.PlayerCharacter;
import game.Model.decorator.DecoratorManager;
import game.Model.decorator.DecoratorManager.PlayerDecoratorType;
import game.Model.engine.GameWorld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Dialog for selecting and applying decorators to player characters.
 * Players can choose up to 2 decorators during character creation.
 *
 */
public class DecoratorSelectionDialog extends JDialog {

    private static final int MAX_DECORATORS = 2;
    private static final int DECORATOR_DURATION = 300; // 5 minutes

    private PlayerCharacter player;
    private JCheckBox[] decoratorCheckboxes;
    private JTextArea descriptionArea;
    private JButton applyButton;
    private int selectedCount = 0;

    public DecoratorSelectionDialog(Frame parent, PlayerCharacter player) {
        super(parent, "Select Power-ups (Max " + MAX_DECORATORS + ")", true);
        this.player = player;
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Choose up to " + MAX_DECORATORS + " power-ups for " + player.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Decorator selection panel
        JPanel decoratorPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        decoratorPanel.setBorder(BorderFactory.createTitledBorder("Available Power-ups"));

        PlayerDecoratorType[] types = PlayerDecoratorType.values();
        decoratorCheckboxes = new JCheckBox[types.length];

        for (int i = 0; i < types.length; i++) {
            PlayerDecoratorType type = types[i];
            JCheckBox checkbox = new JCheckBox(type.getDisplayName());
            checkbox.setActionCommand(type.name());
            checkbox.addActionListener(this::onCheckboxChanged);
            decoratorCheckboxes[i] = checkbox;
            decoratorPanel.add(checkbox);
        }

        mainPanel.add(decoratorPanel, BorderLayout.WEST);

        // Description panel
        descriptionArea = new JTextArea(10, 40);
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setText("Select a power-up to see its description.");

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        applyButton = new JButton("Apply Selected");
        applyButton.setEnabled(false);
        applyButton.addActionListener(e -> applyDecorators());
        buttonPanel.add(applyButton);

        JButton skipButton = new JButton("Skip");
        skipButton.addActionListener(e -> dispose());
        buttonPanel.add(skipButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add descriptions when hovering
        setupDescriptions();
    }

    private void onCheckboxChanged(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();

        // Update selected count
        selectedCount = 0;
        for (JCheckBox checkbox : decoratorCheckboxes) {
            if (checkbox.isSelected()) {
                selectedCount++;
            }
        }

        // Disable unchecked boxes if max selected
        if (selectedCount >= MAX_DECORATORS) {
            for (JCheckBox checkbox : decoratorCheckboxes) {
                if (!checkbox.isSelected()) {
                    checkbox.setEnabled(false);
                }
            }
        } else {
            for (JCheckBox checkbox : decoratorCheckboxes) {
                checkbox.setEnabled(true);
            }
        }

        // Update apply button
        applyButton.setEnabled(selectedCount > 0);

        // Update description
        if (source.isSelected()) {
            updateDescription(PlayerDecoratorType.valueOf(source.getActionCommand()));
        }
    }

    private void setupDescriptions() {
        for (int i = 0; i < decoratorCheckboxes.length; i++) {
            JCheckBox checkbox = decoratorCheckboxes[i];
            PlayerDecoratorType type = PlayerDecoratorType.valueOf(checkbox.getActionCommand());

            checkbox.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    updateDescription(type);
                }
            });
        }
    }

    private void updateDescription(PlayerDecoratorType type) {
        String description = switch (type) {
            case BOOSTED_ATTACK -> "Boosted Attack:\n" +
                    "Increases all attack damage by 50%.\n" +
                    "Perfect for aggressive playstyles.\n" +
                    "Duration: " + DECORATOR_DURATION + " seconds";

            case SHIELDED -> "Shield:\n" +
                    "Blocks the first hit in each round completely.\n" +
                    "Reduces all subsequent damage by 50%.\n" +
                    "Ideal for defensive strategies.\n" +
                    "Duration: " + DECORATOR_DURATION + " seconds";

            case SPEED_BOOST -> "Speed Boost:\n" +
                    "Allows two actions per turn.\n" +
                    "Great for hit-and-run tactics.\n" +
                    "Duration: " + DECORATOR_DURATION + " seconds";

            case REGENERATION -> "Regeneration:\n" +
                    "Heals 2% of max health every 3 seconds.\n" +
                    "Excellent for sustained battles.\n" +
                    "Duration: " + DECORATOR_DURATION + " seconds";

            case MAGIC_AMPLIFIER -> "Magic Amplifier:\n" +
                    "Increases magical damage by 40%.\n" +
                    "Only effective for magic users (Mage).\n" +
                    "Duration: " + DECORATOR_DURATION + " seconds";
        };

        descriptionArea.setText(description);
    }

    private void applyDecorators() {
        DecoratorManager manager = DecoratorManager.getInstance();

        for (JCheckBox checkbox : decoratorCheckboxes) {
            if (checkbox.isSelected()) {
                PlayerDecoratorType type = PlayerDecoratorType.valueOf(checkbox.getActionCommand());
                manager.applyPlayerDecorator(player, type, DECORATOR_DURATION);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Power-ups applied successfully!\n" +
                        "They will last for " + DECORATOR_DURATION + " seconds.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}