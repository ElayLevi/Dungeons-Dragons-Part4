package game.View.gui;
import javax.swing.*;
import java.awt.*;
import game.Controller.GameObserver;
import game.Model.engine.GameWorld;
import game.Model.characters.PlayerCharacter;

/**
 */
public class StatusPanel extends JPanel implements GameObserver {
    private final GameWorld world;
    private final JLabel lblName     = new JLabel();
    private final JLabel lblHealth   = new JLabel();
    private final JLabel lblPower    = new JLabel();
    private final JLabel lblTreasure = new JLabel();

    public StatusPanel(GameWorld world) {
        this.world = world;
        world.registerObserver(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, world.getMap().getNumRows() * 64));

        add(new JLabel("Player Status"));
        add(Box.createVerticalStrut(10));
        add(lblName);
        add(lblHealth);
        add(lblPower);
        add(lblTreasure);

        onModelChanged();
    }

    @Override
    public void onModelChanged() {
        PlayerCharacter p = world.getPlayers().get(0);
        lblName.setText("Player: " + p.getName());
        lblHealth.setText("Health: " + p.getHealth());
        lblPower.setText("Power: "  + p.getPower());
        lblTreasure.setText("Treasure: " + p.getTreasurePoints());
    }
}