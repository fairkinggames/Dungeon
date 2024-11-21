package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class ChasingEnemy extends Enemy{
    private float speed;
    private boolean isKnockedBack;
    private long knockbackStartTime;
    private static final long KNOCKBACK_DURATION = 300_000_000L;
    public ChasingEnemy(float x, float y, float width, float height, int health, float speed) {
        super(x, y, width, height, health);
        this.speed = speed;
    }
    public void update(float delta, Player player) {
        if (isKnockedBack) {
            // Check if knockback time has passed
            if (TimeUtils.nanoTime() - knockbackStartTime > KNOCKBACK_DURATION) {
                isKnockedBack = false;  // Reset knockback
            }
            return;  // Skip further movement during knockback
        }
        // Calculate the direction vector from this enemy to the player
        float directionX = player.getX() - enemyRect.x;
        float directionY = player.getY() - enemyRect.y;

        // Calculate the distance to normalize the direction
        float distance = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        // Normalize the direction and move the enemy towards the player
        if (distance > 0) {  // Prevent division by zero
            enemyRect.x += (directionX / distance) * speed * delta;
            enemyRect.y += (directionY / distance) * speed * delta;
        }
        if (enemyRect.overlaps(player.getPlayerRect())) {
            // Trigger knockback effect
            isKnockedBack = true;
            knockbackStartTime = TimeUtils.nanoTime();

            // Apply knockback by moving the enemy away from the player
            enemyRect.x -= (directionX / distance) * 2;  // Adjust knockback distance as needed
            enemyRect.y -= (directionY / distance) * 2;
        }
    }
}
