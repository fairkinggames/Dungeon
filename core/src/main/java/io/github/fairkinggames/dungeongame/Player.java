package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private int maxHP;
    private int health;
    private String facingDirection = "DOWN";
    private Rectangle playerRect;
    private float attackRange = 50;
    private float attackWidth = 64;
    private Texture swordLeft;
    private Texture swordRight;
    private Texture swordUp;
    private Texture swordDown;


    public Player(float x, float y, float width, float height) {
        maxHP = 100;
        health = 100;
        playerRect = new Rectangle(x, y, width, height);

        // Let's set this as default weapon for now.
        swordDown = new Texture(Gdx.files.internal("AIsword_D.png"));
        swordUp = new Texture(Gdx.files.internal("AIsword_U.png"));
        swordLeft = new Texture(Gdx.files.internal("AIsword_L.png"));
        swordRight = new Texture(Gdx.files.internal("AIsword_R.png"));
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            // TODO Handle death
        }
    }
    public void render(SpriteBatch batch, Texture playerTexture) {
        batch.draw(playerTexture, playerRect.x, playerRect.y, playerRect.width, playerRect.height);
        drawSword(batch);
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
    public int getMaxHP(){
        return maxHP;
    }
    public int getHealth() {
        return health;
    }
    public Rectangle getPlayerRect(){
        return playerRect;
    }
    public void setX(float newX){
        playerRect.x = newX;
    }
    public void setY(float newY){
        playerRect.y = newY;
    }
    public void moveLeft(float delta) {
        playerRect.x -= 200 * delta;
        facingDirection = "LEFT";
    }

    public void moveRight(float delta) {
        playerRect.x += 200 * delta;
        facingDirection = "RIGHT";
    }

    public void moveUp(float delta) {
        playerRect.y += 200 * delta;
        facingDirection = "UP";
    }

    public void moveDown(float delta) {
        playerRect.y -= 200 * delta;
        facingDirection = "DOWN";
    }

    public String getFacingDirection() {
        return facingDirection;
    }

    public Rectangle getAttackHitbox() {
        Rectangle attackHitbox = new Rectangle();
        switch (facingDirection) {
            case "LEFT":
                attackHitbox.set(playerRect.x - attackRange, playerRect.y, attackRange, playerRect.height);
                break;

            case "RIGHT":
                attackHitbox.set(playerRect.x + playerRect.width, playerRect.y, attackRange, playerRect.height);
                break;

            case "UP":
                attackHitbox.set(playerRect.x, playerRect.y + playerRect.height, playerRect.width, attackRange);
                break;

            case "DOWN":
                attackHitbox.set(playerRect.x, playerRect.y - attackRange, playerRect.width, attackRange);
                break;
        }
        return attackHitbox;
    }

    // Not a perfect method for different types of weapons, will change when implementing other weapons.
    private void drawSword(SpriteBatch batch) {
        float swordOffsetX = 0;
        float swordOffsetY = 0;
        float swordWidth = 64;
        float swordHeight = 64;

        Texture swordImage = swordDown;

        switch (facingDirection) {
            case "LEFT":
                swordImage = swordLeft;
                swordOffsetX = -swordWidth;  // Sword to the left of the player
                swordOffsetY = playerRect.height / 2 - swordHeight / 2;  // Vertically centered
                break;
            case "RIGHT":
                swordImage = swordRight;
                swordOffsetX = playerRect.width;  // Sword to the right of the player
                swordOffsetY = playerRect.height / 2 - swordHeight / 2;  // Vertically centered
                break;
            case "UP":
                swordImage = swordUp;
                swordOffsetX = playerRect.width / 2 - swordWidth / 2;  // Horizontally centered
                swordOffsetY = playerRect.height;  // Sword above the player
                break;
            case "DOWN":
                swordImage = swordDown;
                swordOffsetX = playerRect.width / 2 - swordWidth / 2;  // Horizontally centered
                swordOffsetY = -swordHeight;  // Sword below the player
                break;
        }

        // Draw the sword at the calculated position
        batch.draw(swordImage, playerRect.x + swordOffsetX, playerRect.y + swordOffsetY, swordWidth, swordHeight);
    }
}
