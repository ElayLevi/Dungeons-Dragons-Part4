package game.View.gui;
import game.Model.builder.PlayerCharacterBuilder;
import game.Model.characters.PlayerCharacter;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Hashtable;

public class CharacterCreationDialog extends JDialog {
    private final JTextField nameField;
    private final JComboBox<String> classCombo;
    private final JSlider healthSlider;
    private final JSlider powerSlider;
    private final JSlider evasionSlider;
    private final JLabel totalLabel;
    private final JButton createBtn;

    // will hold the result when showDialog() returns
    private PlayerCharacter result;

    public CharacterCreationDialog(Frame owner) {
        super(owner, "Create Your Character", true);

        // --- Name & Class row ---------------------------------------
        nameField = new JTextField(15);
        classCombo = new JComboBox<>(new String[]{"Warrior","Mage","Archer"});

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Name:"));
        top.add(nameField);
        top.add(new JLabel("Class:"));
        top.add(classCombo);

        // --- Sliders & Total ----------------------------------------
        healthSlider  = createAttributeSlider();
        powerSlider   = createAttributeSlider();
        evasionSlider = createAttributeSlider();
        totalLabel    = new JLabel("Total Points: 0 (Must be 0)");

        ChangeListener sliderListener = e -> updateTotalAndButton();
        healthSlider.addChangeListener(sliderListener);
        powerSlider.addChangeListener(sliderListener);
        evasionSlider.addChangeListener(sliderListener);

        // also watch for name changes
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTotalAndButton(); }
            public void removeUpdate(DocumentEvent e) { updateTotalAndButton(); }
            public void changedUpdate(DocumentEvent e) { updateTotalAndButton(); }
        });

        JPanel sliders = new JPanel(new GridLayout(0,1,5,5));
        sliders.add(labeled("Health",  healthSlider));
        sliders.add(labeled("Power",   powerSlider));
        sliders.add(labeled("Evasion", evasionSlider));
        sliders.add(totalLabel);

        // --- Buttons ------------------------------------------------
        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            healthSlider.setValue(0);
            powerSlider.setValue(0);
            evasionSlider.setValue(0);
        });

        createBtn = new JButton("Create Character");
        createBtn.setEnabled(false);
        createBtn.addActionListener(e -> {
            // 1) grab & validate the name
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name may not be blank");
                return;
            }

            // 2) set up the builder with name + class
            PlayerCharacterBuilder b = new PlayerCharacterBuilder()
                    .withName(name);
            switch ((String)classCombo.getSelectedItem()) {
                case "Warrior" -> b.asWarrior();
                case "Mage"    -> b.asMage();
                case "Archer"  -> b.asArcher();
                default        -> throw new IllegalStateException("Unknown class");
            }

            // 3) apply your slider adjustments
            b.withHealth(        healthSlider.getValue())
                    .withPower(         powerSlider.getValue())
                    .withEvasionChance( evasionSlider.getValue());

            // 4) actually build the character
            result = b.build();

            // 5) close the dialog so showDialog() can return
            dispose();
        });


        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            result = null;
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(resetBtn);
        buttons.add(createBtn);
        buttons.add(cancelBtn);

        // --- Layout -------------------------------------------------
        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(top,     BorderLayout.NORTH);
        getContentPane().add(sliders, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public PlayerCharacter showDialog() {
        setVisible(true);

        if (result != null) {
            // Ask if player wants to select decorators
            int option = JOptionPane.showConfirmDialog(null,
                    "Would you like to select starting power-ups?",
                    "Power-ups",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                DecoratorSelectionDialog decoratorDialog = new DecoratorSelectionDialog(null, result);
                decoratorDialog.setVisible(true);
            }
        }

        return result;
    }

    private void updateTotalAndButton() {
        int sum = healthSlider.getValue()
                + powerSlider.getValue()
                + evasionSlider.getValue();
        totalLabel.setText("Total Points: " + sum + " (Must be 0)");

        boolean nameOk = !nameField.getText().trim().isEmpty();
        createBtn.setEnabled(nameOk && sum == 0);
    }

    private JComponent labeled(String text, JSlider slider) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.add(new JLabel(text), BorderLayout.WEST);
        p.add(slider, BorderLayout.CENTER);
        return p;
    }

    private JSlider createAttributeSlider() {
        JSlider s = new JSlider(-2, 3, 0);
        s.setMajorTickSpacing(1);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        s.setSnapToTicks(true);

        Hashtable<Integer,JLabel> labels = new Hashtable<>();
        for (int i = s.getMinimum(); i <= s.getMaximum(); i++) {
            labels.put(i, new JLabel(Integer.toString(i)));
        }
        s.setLabelTable(labels);
        return s;
    }
}
