package io.github.fairkinggames.dungeongame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Intersector;

public class GameScreen implements Screen {
    final Dungeon game;

    private Map<String, Room> rooms;
    private Room currentRoom;
    private final int ROOM_WIDTH = 1280;
    // All these below to be removed
    Music rainMusic;
    OrthographicCamera camera;

    // Game Assets
    Texture backgroundImage;
    Texture rockImage;
    Texture treeImage;
    Texture playerNormalStance;
    Texture playerAttackStance;
    Texture currentPlayerStance;

    Texture bombImage;
    Texture explosionImage;
    Player player;
    Array<Enemy> enemies;
    Array<Obstacle> obstacles;

    Array<Enemy> room1Enemies;
    Array<Obstacle> room1Obstacles;

    Array<Enemy> room2Enemies;
    Array<Obstacle> room2Obstacles;

    // list of rocks and trees to be created. List<GameObject> obstacles;



    float lastBombDropTime = 0.0f;  // Time of the last bomb drop
    float cooldownTime = 2f;
    float hpBarWidth = 64;
    float hpBarHeight = 10;

    // To draw HP bar
    ShapeRenderer shapeRenderer;

    Array<Bomb> bombs;


    public GameScreen(final Dungeon game) {
        this.game = game;

        shapeRenderer = new ShapeRenderer();

        bombImage = new Texture(Gdx.files.internal("AIbomb.png"));
        explosionImage = new Texture(Gdx.files.internal("AIexplosion.png"));

        // load the images for the background, rock, tree, and player class
        rockImage = new Texture(Gdx.files.internal("ph_rock.png"));
        treeImage = new Texture(Gdx.files.internal("ph_tree.png"));
        playerNormalStance = new Texture(Gdx.files.internal("AIpaladin.png"));
        playerAttackStance = new Texture(Gdx.files.internal("AIpaladinAtk.png"));
        currentPlayerStance = playerNormalStance;
        backgroundImage = new Texture(Gdx.files.internal("ph_bgyellow.png"));

        // load the drop sound effect and the rain background "music"
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        rainMusic.setVolume(0.5f);
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);


        // create a Rectangle to logically represent the player
        player = new Player(840, 360, 64, 64);
        enemies = new Array<>();
        obstacles = new Array<>();

        bombs = new Array<>();

        initializeRooms();

    }

    @Override
    public void render(float delta) {

        if (player.getHealth() <= 0) {  // Check if player's HP is zero
            game.setScreen(new MainMenuScreen(game));  // Transition to main menu
            dispose();  // Dispose of resources to avoid memory leaks
            return;  // Stop further processing in this frame
        }

        // clear the screen with a dark blue color.
        ScreenUtils.clear(Color.BLACK);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // Get the current time in seconds

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0, 1280, 720);
        for (Obstacle obstacle : currentRoom.getObstacles()) {
            if (obstacle instanceof Tree) {
                obstacle.render(game.batch, treeImage);
            } else if (obstacle instanceof Rock) {
                obstacle.render(game.batch, rockImage);
            }
        }

        for (Enemy enemy : currentRoom.getEnemies()) {
            if (enemy.isAlive() && enemy instanceof ChasingEnemy) {
                ((ChasingEnemy) enemy).update(delta, player);  // Pass player to chasing enemy
            } else {
                enemy.update(delta);  // Stationary enemy with no movement
            }
            enemy.render(game.batch);
        }
        game.font.draw(game.batch, "Your HP: " + player.getHealth(), 0, 480);
        player.render(game.batch, currentPlayerStance);
        checkBomb();
        game.batch.end();

        //Careful with these lines below. They must stay after batch.end. Caused issue when put these game logic and draw HP inside.
        drawHPBar();
        drawEnemyHPBar();

        checkCollision();
        checkDamage();
        updatePlayer(delta);
        checkRoomTransition();


        // make sure the player stays within the screen bounds
        if (player.getX() < 0)
            player.setX(0);
        if (player.getX() > 1280 - 64)
            player.setX(1280 - 64);
    }
    private void initializeRooms() {
        rooms = new HashMap<>();

        room1Enemies = new Array<>();
        room1Enemies.add(new Enemy(200, 200, 64, 64, 100));
        room1Enemies.add(new Enemy(400, 400, 64, 64, 100));
        room1Enemies.add(new ChasingEnemy(500, 500, 64, 64, 50, 80));

        room1Obstacles = new Array<>();
        room1Obstacles.add(new Tree(100, 100, 64, 64));
        room1Obstacles.add(new Rock(400, 200, 64, 64));

        rooms.put("Room1", new Room(room1Obstacles, room1Enemies));

        room2Enemies = new Array<>();
        room2Enemies.add(new Enemy(300, 200, 64, 64, 100));
        room2Enemies.add(new Enemy(500, 400, 64, 64, 100));
        room2Enemies.add(new ChasingEnemy(600, 500, 64, 64, 50, 80));

        room2Obstacles = new Array<>();
        room2Obstacles.add(new Tree(150, 150, 64, 64));
        room2Obstacles.add(new Rock(450, 250, 64, 64));
        rooms.put("Room2", new Room(room2Obstacles, room2Enemies));

        currentRoom = rooms.get("Room1");
    }


    private void renderRoom1Objects() {
        // Draw obstacles, enemies, etc., specific to Room 1
    }

    private void renderRoom2Objects() {
        // Draw obstacles, enemies, etc., specific to Room 2
    }
    private void updatePlayer(float delta) {
        float currentTime = TimeUtils.nanoTime() / 1e9f;
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            player.moveLeft(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            player.moveRight(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.UP))
            player.moveUp(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            player.moveDown(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.A)){

            player.attackEnemies(currentRoom.getEnemies());
            currentPlayerStance = playerAttackStance;
        } else {
            currentPlayerStance = playerNormalStance;
        }
        if (Gdx.input.isKeyPressed(Keys.E) && currentTime - lastBombDropTime >= cooldownTime) {
            bombs.add(new Bomb(player.getX(), player.getY(), 64, 64));
            lastBombDropTime = currentTime;
        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        bombs.clear();       // Clear bombs placed in the previous room
        enemies.clear();     // Clear enemies from the previous room
        obstacles.clear();
        playerNormalStance.dispose();
        playerAttackStance.dispose();
        treeImage.dispose();
        rockImage.dispose();
        bombImage.dispose();
        explosionImage.dispose();
        rainMusic.dispose();
        backgroundImage.dispose();
        shapeRenderer.dispose();
    }
    private void checkCollision(){
        for (Obstacle obstacle : currentRoom.getObstacles()) {
            if (player.getPlayerRect().overlaps(obstacle.getRect())) {
                movePlayerBack(player, obstacle);
            }
        }
    }

    private void checkDamage(){
        for (Enemy enemy : currentRoom.getEnemies()) {
            if (enemy.isAlive()) {
                enemy.attackPlayer(player);
                if(player.getPlayerRect().overlaps(enemy.getRect())){
                    movePlayerBack(player, enemy);
                }
            }
        }
    }

    private void movePlayerBack(GameObject movingObject, GameObject staticObject) {
        Rectangle movingRect = movingObject.getRect();
        Rectangle staticRect = staticObject.getRect();

        float overlapX = Math.min(movingRect.x + movingRect.width, staticRect.x + staticRect.width)
            - Math.max(movingRect.x, staticRect.x);
        float overlapY = Math.min(movingRect.y + movingRect.height, staticRect.y + staticRect.height)
            - Math.max(movingRect.y, staticRect.y);

        // Determine the axis with the smallest overlap
        if (overlapX < overlapY) {
            if (movingRect.x < staticRect.x) {
                movingObject.setX(movingObject.getX() - overlapX); // Push left
            } else {
                movingObject.setX(movingObject.getX() + overlapX); // Push right
            }
        } else {
            if (movingRect.y < staticRect.y) {
                movingObject.setY(movingObject.getY() - overlapY); // Push down
            } else {
                movingObject.setY(movingObject.getY() + overlapY); // Push up
            }
        }

    }

    private void drawHPBar() {
        // Calculate the width of the HP bar based on the player's current HP
        float currentHPWidth = (player.getHealth() / (float)player.getMaxHP()) * hpBarWidth;

        // Set the color and draw the HP bar above the player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background of the HP bar (gray)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player.getX(), player.getY() + player.getHeight() + 10, hpBarWidth, hpBarHeight);

        // Draw the actual HP bar (red)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(player.getX(), player.getY() + player.getHeight() + 10, currentHPWidth, hpBarHeight);

        shapeRenderer.end();
    }

    private void drawEnemyHPBar() {
        for (Enemy enemy : currentRoom.getEnemies()) {
            // Calculate the width of the HP bar based on the player's current HP
            float currentHPWidth = (enemy.getHealth() / (float)enemy.getMaxHp()) * hpBarWidth;
            // Set the color and draw the HP bar above the player
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Draw the background of the HP bar (gray)
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemy.getX(), enemy.getY() + enemy.getHeight() + 10, hpBarWidth, hpBarHeight);

            // Draw the actual HP bar (red)
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(enemy.getX(), enemy.getY() + enemy.getHeight() + 10, currentHPWidth, hpBarHeight);

            shapeRenderer.end();
        }

    }

    private void surroundWithTrees() {
        int treeWidth = 64;
        int treeHeight = 64;

        // Screen dimensions
        int screenWidth = 1280;
        int screenHeight = 720;
        for (int x = 0; x < screenWidth; x += treeWidth) {
            // Top edge
            Obstacle treeTop = new Tree(x, screenHeight - treeHeight, treeWidth, treeHeight);
            obstacles.add(treeTop);

            // Bottom edge
            Obstacle treeBottom = new Tree(x, 0, treeWidth, treeHeight);
            obstacles.add(treeBottom);
        }

    }

    private void checkBomb() {
        Iterator<Bomb> iter = bombs.iterator();
        while (iter.hasNext()) {
            Bomb bomb = iter.next();
            // Check if the bomb is in the exploding state
            if (bomb.isExploding()) {
                // Only apply damage once during the explosion
                if (!bomb.damageApplied) {
                    // Check distance to the player
                    float playerDistance = distance(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2,
                        bomb.getRect().x + bomb.getRect().width / 2, bomb.getRect().y + bomb.getRect().height / 2);
                    if (playerDistance < bomb.getExplosionRadius()) {
                        // Apply damage to the player once
                        player.takeDamage(50);
                    }

                    // Check distance to enemies
                    for (Enemy enemy : currentRoom.getEnemies()) {
                        if (enemy.isAlive()){
                            float enemyDistance = distance(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2,
                                bomb.getRect().x + bomb.getRect().width / 2, bomb.getRect().y + bomb.getRect().height / 2);

                            if (enemyDistance < bomb.getExplosionRadius()) {
                                // Apply damage to the enemy once
                                enemy.takeDamage(20);  // Adjust damage amount as needed
                            }
                        }
                    }

                    // Mark the bomb as having applied damage
                    bomb.damageApplied = true;
                }
                // Show explosion image for a short time
                if (TimeUtils.nanoTime() - bomb.getExplosionStartTime() < 1e9) {  // Show for 0.5 seconds
                    game.batch.draw(explosionImage, bomb.getRect().x, bomb.getRect().y, bomb.getRect().width, bomb.getRect().height);

                } else {
                    // Explosion time is over, remove the bomb
                    iter.remove();
                }
            } else {
                // Regular bomb state
                game.batch.draw(bombImage, bomb.getRect().x, bomb.getRect().y, bomb.getRect().width, bomb.getRect().height);

                // Check if 3 seconds (3e9 nanoseconds) have passed since the bomb was placed
                if (TimeUtils.nanoTime() - bomb.getSpawnTime() > 3e9) {
                    // Bomb should start exploding
                    bomb.isExploding = true;
                    bomb.explosionStartTime = TimeUtils.nanoTime();  // Record explosion start time
                }
            }
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }


    private void checkRoomTransition() {
        if (currentRoom == rooms.get("Room1") && player.getX() > 1200) {

            // Move to Room 2
            currentRoom = rooms.get("Room2");
            player.setPosition(81, player.getY());  // Wrap player to the left side of Room 2
        } else if (currentRoom == rooms.get("Room2") && player.getX() < 80) {

            // Move back to Room 1
            currentRoom = rooms.get("Room1");
            player.setPosition(1199, player.getY());  // Wrap player to the right side of Room 1
        }
    }


}
