package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class Bomb {
    public Rectangle bombRect;  // Obstacle's position and size
    public long spawnTime;
    public boolean damageApplied = false;
    public boolean isExploding = false;  // Whether the bomb is in the exploding state
    public long explosionStartTime;  // Time when the explosion started (for timing)
    public float explosionRadius = 100f;

    public Bomb(float x, float y, float width, float height) {
        bombRect = new Rectangle(x, y, width, height);  // Initialize the obstacle's position and size
        spawnTime = TimeUtils.nanoTime();
    }

    public void render(SpriteBatch batch, Texture bombTexture) {
        batch.draw(bombTexture, bombRect.x, bombRect.y, bombRect.width, bombRect.height);
    }
    public Rectangle getRect() {
        return bombRect;
    }
    public long getSpawnTime() {
        return spawnTime;
    }
    public boolean isExploding() {
        return isExploding;
    }
    public long getExplosionStartTime() {
        return explosionStartTime;
    }
    public float getExplosionRadius() {
        return explosionRadius;
    }
}
