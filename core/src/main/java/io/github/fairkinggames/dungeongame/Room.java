package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.utils.Array;

public class Room {
    private Array<Obstacle> obstacles;
    private Array<Obstacle> OGObstacles;
    private Array<Enemy> enemies;
    private final Array<Enemy> OGEnemies;
    private boolean isCleared;

    public Room(Array<Obstacle> obstacles, Array<Enemy> enemies) {
        this.obstacles = obstacles;
        this.enemies = enemies;
        this.isCleared = false;
        this.OGEnemies = new Array<>();
        for (Enemy enemy : enemies) {
            Enemy copiedEnemy = enemy.copy();
            this.OGEnemies.add(copiedEnemy); // Add to original enemies
        }
    }

    public Array<Obstacle> getObstacles() {
        return obstacles;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
    public Array<Enemy> getOGEnemies() {
        return OGEnemies;
    }


    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    public void checkRoomCleared() {
        if (!isCleared) {
            isCleared = true; // Assume the room is cleared
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    isCleared = false; // If any enemy is still alive, the room is not cleared
                    break; // No need to check further
                }
            }
        }
    }

    public void setEnemies(Array<Enemy> newEnemies) {
        this.enemies = newEnemies;
    }
}
