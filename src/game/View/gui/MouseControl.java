package game.View.gui;
import game.Controller.GameController;
import game.Model.map.Position;
import game.Model.map.GameMap;
import game.Model.engine.GameWorld;
import game.Model.core.GameEntity;
import game.Model.items.GameItem;
import game.Model.combat.Combatant;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Handles mouse clicks on a single map‐cell, forwarding left‐clicks
 * to the GameController and showing info popups on right‐clicks.
 */
public class MouseControl extends MouseAdapter {
    private final Position pos;
    private final GameController controller;
    private final GameWorld model = GameWorld.getInstance();

    /**
     * @param pos        The map‐cell position this listener is attached to
     * @param controller Your GameController instance
     */
    public MouseControl(Position pos, GameController controller) {
        this.pos = pos;
        this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // delegate main action (move, attack, pickup)
            controller.onLeftClick(pos);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // show the info popup exactly where the user clicked
            showInfoPopup(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Constructs and shows a JPopupMenu with details about whatever's
     * on this cell: enemy name + HP, item description, or "Empty Tile".
     */
    private void showInfoPopup(Component src, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        GameMap map = model.getMap();
        List<GameEntity> ents = map.getEntities(pos);

        if (ents.isEmpty()) {
            popup.add(new JMenuItem("Empty Tile"));
        } else {
            for (GameEntity e : ents) {
                // if it's a character/enemy, cast to Combatant
                if (e instanceof Combatant c) {
                    popup.add(new JMenuItem(c.getName() + " (HP: " + c.getHealth() + ")"));
                }
                // if it's an item, cast to GameItem for description
                else if (e instanceof GameItem gi) {
                    popup.add(new JMenuItem(gi.getDescription()));
                }
                // fallback: show its display symbol
                else {
                    popup.add(new JMenuItem(e.getDisplaySymbol()));
                }
            }
        }
        popup.show(src, x, y);
    }
}
