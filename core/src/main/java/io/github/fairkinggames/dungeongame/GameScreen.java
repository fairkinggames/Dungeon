package io.github.fairkinggames.dungeongame;

import java.util.Iterator;

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

public class GameScreen implements Screen {
    final Dungeon game;

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

    // list of rocks and trees to be created. List<GameObject> obstacles;

    Array<Obstacle> obstacles;

    long lastDamageTimeEnemy = 0;
    long lastDamageTimePlayer = 0;
    long damageIntervalEnemy = 2000;
    long damageIntervalPlayer = 1000;
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
        enemies.add(new Enemy(200, 200, 64, 64, 100));
        enemies.add(new Enemy(400, 400, 64, 64, 100));
        enemies.add(new ChasingEnemy(500, 500, 64, 64, 50, 80));

        obstacles = new Array<>();
        obstacles.add(new Tree(100, 100, 64, 64));  // Tree
        obstacles.add(new Rock(400, 200, 64, 64));  // Rock

        bombs = new Array<>();

        surroundWithTrees();

    }

    @Override
    public void render(float delta) {
        if (player.getHealth() <= 0) {  // Check if player's HP is zero
            game.setScreen(new MainMenuScreen(game));  // Transition to main menu
            dispose();  // Dispose of resources to avoid memory leaks
            return;  // Stop further processing in this frame
        }
        for (Enemy enemy : enemies) {
            //TODO im calling a lot of isAlive() at the moment, unsure if there is a better way to handle these in enemy class.
            if (enemy.isAlive() && enemy instanceof ChasingEnemy) {
                ((ChasingEnemy) enemy).update(delta, player);  // Pass player to chasing enemy
            } else {
                enemy.update(delta);  // Stationary enemy with no movement
            }
        }
        // clear the screen with a dark blue color.
        ScreenUtils.clear(Color.BLACK);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        // Get the current time in seconds
        float currentTime = TimeUtils.nanoTime() / 1e9f;
        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();


        game.batch.draw(backgroundImage, 0, 0, 1280, 720);
        game.font.draw(game.batch, "Your HP: " + player.getHealth(), 0, 480);
        player.render(game.batch, currentPlayerStance);

        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof Tree) {
                obstacle.render(game.batch, treeImage);
            } else if (obstacle instanceof Rock) {
                obstacle.render(game.batch, rockImage);
            }
        }

        for (Enemy enemy : enemies) {
            enemy.render(game.batch);
        }
        checkBomb();

        game.batch.end();

        drawHPBar();
        drawEnemyHPBar();


        if (Gdx.input.isKeyPressed(Keys.LEFT))
            player.moveLeft(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            player.moveRight(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.UP))
            player.moveUp(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            player.moveDown(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.A)){

            player.attackEnemies(enemies);
            currentPlayerStance = playerAttackStance;
        } else {
            currentPlayerStance = playerNormalStance;
        }
        if (Gdx.input.isKeyPressed(Keys.E) && currentTime - lastBombDropTime >= cooldownTime) {
            bombs.add(new Bomb(player.getX(), player.getY(), 64, 64));
            lastBombDropTime = currentTime;
        }

        checkDamage();
        checkCollision();


        // make sure the bucket stays within the screen bounds
        if (player.getX() < 0)
            player.setX(0);
        if (player.getX() > 1280 - 64)
            player.setX(1280 - 64);

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
        playerNormalStance.dispose();
        rainMusic.dispose();
        backgroundImage.dispose();
    }
    private void checkCollision(){
        for (Obstacle obstacle : obstacles) {
            if (player.getPlayerRect().overlaps(obstacle.getRect())) {
                movePlayerBack();
            }
        }
    }

    private void checkDamage(){
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && player.getPlayerRect().overlaps(enemy.getRect())) {
                movePlayerBack();
                if (TimeUtils.timeSinceMillis(lastDamageTimeEnemy) >= damageIntervalEnemy) {
                    player.takeDamage(10); // Player takes 10 damage every 2 seconds
                    lastDamageTimeEnemy = TimeUtils.millis(); // Update the time when the last damage was taken
                }
            }
        }
    }

    private void movePlayerBack() {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.setX(player.getX() + 200 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.setX(player.getX() - 200 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.setY(player.getY() - 200 * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.setY(player.getY() + 200 * Gdx.graphics.getDeltaTime());
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
        for (Enemy enemy : enemies) {
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
                    for (Enemy enemy : enemies) {
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
    // Helper method to check if the enemy is in the direction the player is facing
    private boolean isEnemyInFacingDirection(Player player, Enemy enemy) {
        String direction = player.getFacingDirection();
        Rectangle playerRect = player.getPlayerRect();
        Rectangle enemyRect = enemy.getRect();

        switch (direction) {
            case "LEFT":
                // Enemy must be to the left of the player
                return enemyRect.x < playerRect.x;

            case "RIGHT":
                // Enemy must be to the right of the player
                return enemyRect.x > playerRect.x;

            case "UP":
                // Enemy must be above the player
                return enemyRect.y > playerRect.y;

            case "DOWN":
                // Enemy must be below the player
                return enemyRect.y < playerRect.y;

            default:
                return false;  // If direction is unknown, return false
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

}
