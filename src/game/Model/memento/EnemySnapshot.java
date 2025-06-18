package game.Model.memento;

import game.Model.characters.Enemy;
import game.Model.map.Position;

import java.io.Serializable;

public class EnemySnapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String typeName;
    private final Position position;
    private final int health, power;

    public EnemySnapshot(Enemy e) {
        this.typeName = e.getClass().getSimpleName();
        this.position = new Position(e.getPosition());
        this.health   = e.getHealth();
        this.power    = e.getPower();
    }

    public String   getTypeName()   { return typeName; }
    public Position getPosition()   { return new Position(position); }
    public int      getHealth()     { return health; }
    public int      getPower()      { return power; }
}
