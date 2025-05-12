package game.View.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import game.Model.engine.GameWorld;
import game.Model.items.GameItem;
import game.Model.characters.PlayerCharacter;
import game.Controller.GameObserver;

public class InventoryPanel extends JPanel implements GameObserver {
    private final GameWorld world;
    private final DefaultListModel<GameItem> model = new DefaultListModel<>();
    private final JList<GameItem> list = new JList<>(model);
    private final JButton useBtn = new JButton("Use");

    public InventoryPanel(GameWorld world) {
        this.world = world;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Inventory"));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(useBtn, BorderLayout.SOUTH);

        useBtn.addActionListener(e -> {
            GameItem itm = list.getSelectedValue();
            if (itm != null) {
                world.useItem(itm);
            }
        });

        world.registerObserver(this);
        refresh();
    }

    @Override
    public void onModelChanged() {
        refresh();
    }

    private void refresh() {
        PlayerCharacter p = world.getPlayers().get(0);
        List<GameItem> items = p.getInventory().getItems();

        model.clear();
        for (GameItem it : items) {
            model.addElement(it);
        }
        useBtn.setEnabled(!items.isEmpty());
    }
}
