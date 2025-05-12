package game.View.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.List;


import game.Controller.GameObserver;
import game.Controller.GameController;
import game.Model.characters.*;
import game.Model.engine.GameWorld;
import game.Model.items.*;
import game.Model.map.GameMap;
import game.Model.map.Position;
import game.Model.core.GameEntity;
import game.Model.combat.Combatant;
import game.Model.engine.GameWorld.Action;

public class MapPanel extends JPanel implements GameObserver {
    private static final int TILE_SIZE = 64;

    private final GameWorld world;
    private final GameController controller;
    private final GameMap map;
    private final int rows, cols;
    private final JButton[][] cells;
    private final ImageIcon unknownIcon = loadResource("images/unknown.png");

    public MapPanel(GameWorld world, GameController controller) {
        this.world = world;
        this.controller = controller;
        this.map = world.getMap();

        this.rows = map.getNumRows();
        this.cols = map.getNumCols();
        this.cells = new JButton[rows][cols];

        setLayout(new GridLayout(rows, cols));
        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));

        initGrid();
        world.registerObserver(this);
        refresh();
    }

    private void initGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setBackground(Color.DARK_GRAY);

                Position pos = new Position(r, c);
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            controller.onLeftClick(pos);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            showInfoPopup(pos, btn, e.getX(), e.getY());
                        }
                    }
                });

                cells[r][c] = btn;
                add(btn);
            }
        }
    }

    @Override
    public void onModelChanged() {
        refresh();
    }

    private void refresh() {
        game.Model.engine.GameWorld.Action act = world.getLastAction();
        Position actPos = world.getLastActionPos();
        GameMap map = world.getMap();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton cell = cells[r][c];
                cell.setIcon(null);
                cell.setToolTipText(null);
                cell.setBackground(Color.DARK_GRAY);

                Position pos = new Position(r, c);
                List<GameEntity> ents = map.getEntities(pos);

                GameEntity toDraw = null;

                for (GameEntity e : ents) {
                    if (e instanceof PlayerCharacter pc && pc.getVisible()) {
                        toDraw = e;
                        break;
                    }
                }
                if (toDraw == null) {
                    for (GameEntity e : ents) {
                        if (e.getVisible()
                                && (e instanceof Potion
                                || e instanceof PowerPotion
                                || e instanceof Treasure)) {
                            toDraw = e;
                            break;
                        }
                    }
                }
                if (toDraw == null) {
                    for (GameEntity e : ents) {
                        if (e.getVisible() && e instanceof Enemy) {
                            toDraw = e;
                            break;
                        }
                    }
                }
                if (toDraw == null) {
                    for (GameEntity e : ents) {
                        if (e.getVisible()) {
                            toDraw = e;
                            break;
                        }
                    }
                }

                if (toDraw != null) {
                    cell.setIcon(loadEntityIcon(toDraw));
                    cell.setToolTipText(toDraw.getDisplaySymbol());
                }

                if (actPos != null
                        && pos.getRow() == actPos.getRow()
                        && pos.getCol() == actPos.getCol())
                {
                    if (act == Action.PICKUP) {
                        cell.setBackground(Color.GREEN);
                    } else if (act == Action.COMBAT) {
                        cell.setBackground(Color.RED);
                    } else if (act == Action.MOVE) {
                        cell.setBackground(Color.LIGHT_GRAY);
                    }

                    new Timer(200, ev -> cell.setBackground(Color.DARK_GRAY)) {{
                        setRepeats(false);
                        start();
                    }};
                }
            }
        }

        revalidate();
        repaint();
    }


    private void showInfoPopup(Position pos, Component src, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        for (GameEntity e : map.getEntities(pos)) {
            if (!e.getVisible()) continue;
            if (e instanceof Combatant c) {
                popup.add(new JMenuItem(c.getName() + " (HP:" + c.getHealth() + ")"));
            } else if (e instanceof GameItem gi) {
                popup.add(new JMenuItem(gi.getDescription()));
            } else {
                popup.add(new JMenuItem(e.getDisplaySymbol()));
            }
        }
        if (popup.getComponentCount() == 0) {
            popup.add(new JMenuItem("Empty Tile"));
        }
        popup.show(src, x, y);
    }

    private ImageIcon loadEntityIcon(GameEntity e) {

        String cls = e.getClass().getSimpleName().toLowerCase();
        String file = switch (cls) {
            case "archer" -> "Archer.png";
            case "mage" -> "Mage.png";
            case "warrior" -> "Warrior.png";
            case "dragon" -> "Dragon.png";
            case "goblin" -> "Goblin.png";
            case "orc" -> "Orc.png";
            case "potion" -> "HealthPotion.png";
            case "power potion" -> "PowerPotion.png";
            case "treasure" -> "Treasure.png";
            case "wall" -> "Wall.png";
            default -> null;
        };


        if (file == null) {
            return null;
        }

        java.net.URL url = getClass().getClassLoader().getResource("images/" + file);
        if (url != null) {
            return new ImageIcon(url);
        }

        return new ImageIcon("src/game/Resources/images/" + file);
    }


    private static ImageIcon loadResource(String path) {
        java.net.URL url = MapPanel.class.getClassLoader().getResource(path);
        return (url != null) ? new ImageIcon(url)
                : new ImageIcon("src/game/Resources/" + path);
    }
}
