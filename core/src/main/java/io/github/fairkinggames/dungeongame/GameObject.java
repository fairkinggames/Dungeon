package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    protected float x, y; // Position
    protected float width, height; // Size
    public Rectangle rect;

    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rect = new Rectangle(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Rectangle getRect() {
        return rect;
    }
}
