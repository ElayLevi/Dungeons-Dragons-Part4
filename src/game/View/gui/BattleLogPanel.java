package game.View.gui;

import game.Controller.GameObserver;
import game.Model.engine.GameWorld;
import game.Model.combat.BattleResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BattleLogPanel extends JPanel implements GameObserver {
    private final GameWorld world;
    private final DefaultTableModel model;

    public BattleLogPanel(GameWorld world) {
        this.world = world;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Battle Log"));

        String[] cols = { "Enemy", "Rounds", "Player Dmg", "Enemy Dmg" };
        model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        world.registerObserver(this);
        loadExisting();
    }

    private void loadExisting() {
        List<BattleResult> list = world.getBattleResults();
        for (BattleResult r : list) {
            model.addRow(new Object[]{
                    r.getEnemyName(),
                    r.getRounds(),
                    r.getDmgByPlayer(),
                    r.getDmgByEnemy()
            });
        }
    }

    @Override
    public void onModelChanged() {
        List<BattleResult> list = world.getBattleResults();
        if (list.size() > model.getRowCount()) {
            BattleResult r = list.get(list.size() - 1);
            model.addRow(new Object[]{
                    r.getEnemyName(),
                    r.getRounds(),
                    r.getDmgByPlayer(),
                    r.getDmgByEnemy()
            });
        }
    }
}
