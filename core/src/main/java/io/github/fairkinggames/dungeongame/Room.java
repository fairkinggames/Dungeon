package io.github.fairkinggames.dungeongame;

import com.badlogic.gdx.utils.Array;

public class Room {
    private Array<Obstacle> RoomObstacles;
    private Array<Obstacle> OGObstacles;
    private Array<Enemy> RoomEnemies;
    private Array<Enemy> OGEnemies;
    private boolean isCleared;
    private Room north, south, east, west; // Adjacent rooms

    public Room(Array<Obstacle> obstacles, Array<Enemy> enemies) {
        this.RoomObstacles = new Array<>();
        this.RoomEnemies = new Array<>();
        this.OGEnemies = new Array<>();
        this.OGObstacles = new Array<>();

        for (Enemy enemy : enemies) {
            this.OGEnemies.add(enemy.copy());
            this.RoomEnemies.add(enemy.copy());
        }

        for (Obstacle obstacle : obstacles) {
            this.OGObstacles.add(obstacle);
            this.RoomObstacles.add(obstacle);
        }

        this.isCleared = false;
    }

    public Array<Obstacle> getObstacles() {
        return RoomObstacles;
    }

    public Array<Enemy> getEnemies() {
        return RoomEnemies;
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
            for (Enemy enemy : RoomEnemies) {
                if (enemy.isAlive()) {
                    isCleared = false; // If any enemy is still alive, the room is not cleared
                    break; // No need to check further
                }
            }
        }
    }

    public void setEnemies(Array<Enemy> newEnemies) {
        this.RoomEnemies = newEnemies;
    }

    public void resetRoom() {
        // Clear current enemies and obstacles
        this.RoomEnemies.clear();

        // Deep copy from OGEnemies and OGObstacles
        for (Enemy originalEnemy : OGEnemies) {
            this.RoomEnemies.add(originalEnemy.copy());
        }


        this.isCleared = false; // Mark the room as not cleared
    }

    // Getters and setters for neighbors
    public Room getNorth() { return north; }
    public void setNorth(Room north) { this.north = north; }

    public Room getSouth() { return south; }
    public void setSouth(Room south) { this.south = south; }

    public Room getEast() { return east; }
    public void setEast(Room east) { this.east = east; }

    public Room getWest() { return west; }
    public void setWest(Room west) { this.west = west; }
}
