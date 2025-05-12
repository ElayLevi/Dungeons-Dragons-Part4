package game.Controller;

import game.Model.characters.Enemy;
import game.Model.characters.PlayerCharacter;
import game.Model.items.GameItem;
import game.Model.items.Potion;
import game.Model.items.PowerPotion;
import game.Model.items.Treasure;
import game.Model.map.Position;
import game.Model.core.GameEntity;
import game.Model.engine.GameWorld;

import java.util.List;
public class GameController {
    private final GameWorld world;

    public GameController(GameWorld world) {
        this.world = world;
    }


    public void onLeftClick(Position pos) {
        PlayerCharacter player = world.getPlayers().get(0);
        int dist = player.getPosition().distanceTo(pos);

        for (GameEntity e : world.getMap().getEntities(pos)) {
            if (dist == 1
                    && e.getVisible()
                    && (e instanceof Potion
                    || e instanceof PowerPotion
                    || e instanceof Treasure)) {
                world.pickUpItem((GameItem)e);
                return;
            }
        }

        for (GameEntity e : world.getMap().getEntities(pos)) {
            if (e instanceof Enemy enemy && enemy.getVisible()) {
                world.attack(enemy);
                return;
            }
        }

        boolean moved = world.movePlayer(pos);
        if (!moved) {
            System.out.println("Can't move there!");
        }
    }






    public void onRightClick(Position pos) {
        System.out.println("Info requested at " + pos);
    }

}
