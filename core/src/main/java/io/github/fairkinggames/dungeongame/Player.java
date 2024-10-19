package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private int health;
    public Rectangle playerRect;

    public Player(float x, float y, float width, float height) {
        health = 100;
        playerRect = new Rectangle(x, y, width, height);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            System.out.println("Player is dead!");
            // TODO Handle death
        }
    }
    public void render(SpriteBatch batch, Texture playerTexture) {
        batch.draw(playerTexture, playerRect.x, playerRect.y, playerRect.width, playerRect.height);
    }
    public float getX(){
        return playerRect.x;
    }
    public float getY(){
        return playerRect.y;
    }
    public float getWidth(){
        return playerRect.width;
    }
    public float getHeight(){
        return playerRect.height;
    }
    public void setX(float newX){
        playerRect.x = newX;
    }
    public void setY(float newY){
        playerRect.y = newY;
    }
}