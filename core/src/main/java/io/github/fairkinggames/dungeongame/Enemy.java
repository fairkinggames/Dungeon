package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private int maxHP;
    private int health;
    public Rectangle enemyRect;

    public Enemy(float x, float y, float width, float height) {
        maxHP = 100;
        health = 100;
        enemyRect = new Rectangle(x, y, width, height);
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            // TODO Handle death
        }
    }
    public void render(SpriteBatch batch, Texture enemyTexture) {
        batch.draw(enemyTexture, enemyRect.x, enemyRect.y, enemyRect.width, enemyRect.height);
    }
    public float getX(){
        return enemyRect.x;
    }
    public float getY(){
        return enemyRect.y;
    }
    public float getWidth(){
        return enemyRect.width;
    }
    public float getHeight(){
        return enemyRect.height;
    }
    public int getMaxHP(){
        return maxHP;
    }
    public int getHealth() {
        return health;
    }
    public Rectangle getRect(){
        return enemyRect;
    }
    public void setX(float newX){
        enemyRect.x = newX;
    }
    public void setY(float newY){
        enemyRect.y = newY;
    }
}
