package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    public Rectangle ObstacleRect;  // Obstacle's position and size

    public Obstacle(float x, float y, float width, float height) {
        ObstacleRect = new Rectangle(x, y, width, height);  // Initialize the obstacle's position and size
    }

    public void render(SpriteBatch batch, Texture obstacleTexture) {
        batch.draw(obstacleTexture, ObstacleRect.x, ObstacleRect.y, ObstacleRect.width, ObstacleRect.height);
    }
    public Rectangle getRect() {
        return ObstacleRect;
    }
}

