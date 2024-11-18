package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private int maxHp;
    protected int health;
    protected Rectangle enemyRect;
    private Texture enemyImage;
    private Texture enemyDeath;

    public Enemy(float x, float y, float width, float height, int hp) {
        this.maxHp = hp;
        this.health = hp;
        enemyRect = new Rectangle(x, y, width, height);
        enemyImage = new Texture(Gdx.files.internal("AImonster.png"));
        enemyDeath = new Texture(Gdx.files.internal("AIdead.png"));
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            enemyImage = enemyDeath;
        }
    }
    public void render(SpriteBatch batch) {
        batch.draw(enemyImage, enemyRect.x, enemyRect.y, enemyRect.width, enemyRect.height);
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
    public int getHealth() {
        return health;
    }
    public int getMaxHp(){
        return maxHp;
    }
    public boolean isAlive(){
        return (health>0);
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
    public void update(float delta) {}

}
