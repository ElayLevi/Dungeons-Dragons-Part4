package game.Model.memento;

import game.Model.items.GameItem;    // <— import your GameItem
import game.Model.map.Position;
import java.io.Serializable;

public class ItemSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int index;
    private final String typeName;
    private final Position pos;
    private final boolean visible;

    /** ← new convenience constructor so you can do `.map(ItemSnapshot::new)` */
    public ItemSnapshot(GameItem item) {
        // you can decide what to do with “index” here.  If you don’t care, just zero or -1:
        this(-1,
                item.getClass().getSimpleName(),
                item.getPosition(),
                item.isVisible());
    }

    public ItemSnapshot(int index, String typeName,
                        Position pos, boolean visible) {
        this.index    = index;
        this.typeName = typeName;
        this.pos      = new Position(pos);
        this.visible  = visible;
    }

    public int getIndex()       { return index; }
    public String getTypeName() { return typeName; }
    public Position getPosition() { return new Position(pos); }
    public boolean isVisible()  { return visible; }
}
