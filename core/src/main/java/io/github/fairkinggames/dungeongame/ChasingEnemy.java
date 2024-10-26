package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
public class ChasingEnemy extends Enemy{
    private float speed;
    public ChasingEnemy(float x, float y, float width, float height, int health, float speed) {
        super(x, y, width, height, health);
        this.speed = speed;
    }
    public void update(float delta, Player player) {
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
    }
}
