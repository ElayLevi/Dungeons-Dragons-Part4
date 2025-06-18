package game.Model.memento;

import game.Model.map.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PlayerSnapshot     playerState;
    private final List<EnemySnapshot> enemyStates;
    private final List<ItemSnapshot>  itemStates;
    private final String              lastAction;
    private final Position            lastActionPos;

    public GameMemento(PlayerSnapshot playerState,
                       List<EnemySnapshot> enemyStates,
                       List<ItemSnapshot>  itemStates,
                       String              lastAction,
                       Position            lastActionPos) {
        this.playerState   = playerState;
        this.enemyStates   = new ArrayList<>(enemyStates);
        this.itemStates    = new ArrayList<>(itemStates);
        this.lastAction    = lastAction;
        this.lastActionPos = (lastActionPos != null ? new Position(lastActionPos) : null);
    }

    public PlayerSnapshot       getPlayerState()    { return playerState; }
    public List<EnemySnapshot>  getEnemyStates()    { return Collections.unmodifiableList(enemyStates); }
    public List<ItemSnapshot>   getItemStates()     { return Collections.unmodifiableList(itemStates); }
    public String               getLastAction()     { return lastAction; }
    public Position             getLastActionPos()  { return lastActionPos == null ? null : new Position(lastActionPos); }
}