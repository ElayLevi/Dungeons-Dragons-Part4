package game.Model.decorator.enemy;
import game.Model.characters.Enemy;
import game.Model.characters.PlayerCharacter;
import game.Model.decorator.CharacterDecorator;
import game.Model.engine.GameWorld;
import game.Model.map.Position;
import game.Util.GameLogger;

/**
 * Decorator that causes area damage when the enemy dies.
 * Deals 20% of max health as damage to nearby players.
 */
public class ExplodingEnemyDecorator extends CharacterDecorator {

    private static final double EXPLOSION_DAMAGE_PERCENT = 0.2;
    private static final int EXPLOSION_RANGE = 2;

    public ExplodingEnemyDecorator(Enemy enemy, int durationSeconds) {
        super(enemy, durationSeconds);
        GameLogger.getInstance().log(enemy.enemyDiscription() + " became explosive!");
    }

    @Override
    public boolean isDead() {
        boolean wasDead = super.isDead();

        if (!wasDead && getHealth() <= 0 && isActive()) {
            // Trigger explosion before actually dying
            explode();
        }

        return super.isDead();
    }

    private void explode() {
        GameLogger.getInstance().log(((Enemy)getBaseCharacter()).enemyDiscription() + " explodes!");

        GameWorld world = GameWorld.getInstance();
        Position myPos = getPosition();
        int explosionDamage = (int)(50 * EXPLOSION_DAMAGE_PERCENT); // 50 is enemy default health

        // Damage all players within range
        for (PlayerCharacter player : world.getPlayers()) {
            if (myPos.distanceTo(player.getPosition()) <= EXPLOSION_RANGE) {
                player.takeDamage(explosionDamage);
                GameLogger.getInstance().log("Explosion dealt " + explosionDamage + " damage to " + player.getName());
            }
        }
    }

    @Override
    public String getDecoratorName() {
        return "Exploding";
    }
}