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

        // הגדרת עמודות הטבלה
        String[] cols = { "Enemy", "Rounds", "Player Dmg", "Enemy Dmg" };
        model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // הרשמה כ־Observer וטעינת כל התוצאות הקיימות
        world.registerObserver(this);
        loadExisting();
    }

    // נטען בהפעלה ראשונית את כל התוצאות שכבר נמצאות ב־GameWorld
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

    // קורא פעם אחת עבור כל notifyObservers()
    @Override
    public void onModelChanged() {
        // אם נוספו תוצאות חדשות – הוסף רק את ההפרש
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
