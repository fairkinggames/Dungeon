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
    Texture enemyImage;
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


        bombs = new Array<>();

        bombImage = new Texture(Gdx.files.internal("AIbomb.png"));
        explosionImage = new Texture(Gdx.files.internal("AIexplosion.png"));

        // load the images for the background, rock, tree, and player class
        rockImage = new Texture(Gdx.files.internal("ph_rock.png"));
        treeImage = new Texture(Gdx.files.internal("ph_tree.png"));
        playerNormalStance = new Texture(Gdx.files.internal("AIpaladin.png"));
        playerAttackStance = new Texture(Gdx.files.internal("AIpaladinAtk.png"));
        currentPlayerStance = playerNormalStance;
        enemyImage = new Texture(Gdx.files.internal("AImonster.png"));
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
        enemies.add(new Enemy(200, 200, 64, 64));
        enemies.add(new Enemy(400, 400, 64, 64));

        obstacles = new Array<>();
        obstacles.add(new Tree(100, 100, 64, 64));  // Tree
        obstacles.add(new Rock(400, 200, 64, 64));  // Rock



        surroundWithTrees();

    }

    @Override
    public void render(float delta) {
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

        //Rename this E later, enemy is not a good name, should likely be types of enemies.
        for (Enemy enemy : enemies) {
            enemy.render(game.batch, enemyImage);
        }


        Iterator<Bomb> iter = bombs.iterator();
        while (iter.hasNext()) {
            Bomb bomb = iter.next();
            // Check if the bomb is in the exploding state
            if (bomb.isExploding) {
                // Show explosion image for a short time
                if (TimeUtils.nanoTime() - bomb.explosionStartTime < 500_000_000L) {  // Show for 0.5 seconds
                    game.batch.draw(explosionImage, bomb.rect.x, bomb.rect.y, bomb.rect.width, bomb.rect.height);
                    float playerDistance = distance(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2,
                        bomb.rect.x + bomb.rect.width / 2, bomb.rect.y + bomb.rect.height / 2);
                    if (playerDistance < bomb.explosionRadius) {
                        System.out.println(playerDistance);
                        System.out.println(bomb.explosionRadius);
                        // Apply damage to the player
                        player.takeDamage(50);  // Apply 20 damage (adjust as needed)
                    }
                    for (Enemy enemy : enemies) {
                        float enemyDistance = distance(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2,
                            bomb.rect.x + bomb.rect.width / 2, bomb.rect.y + bomb.rect.height / 2);

                        if (enemyDistance < bomb.explosionRadius) {
                            // Apply damage to the enemy
                            enemy.takeDamage(20);  // Apply 20 damage (adjust as needed)
                        }
                    }
                } else {
                    // Explosion time is over, remove the bomb
                    iter.remove();
                }
            } else {
                // Regular bomb state
                game.batch.draw(bombImage, bomb.rect.x, bomb.rect.y, bomb.rect.width, bomb.rect.height);

                // Check if 3 seconds (3e9 nanoseconds) have passed since the bomb was placed
                if (TimeUtils.nanoTime() - bomb.spawnTime > 3e9) {
                    // Bomb should start exploding
                    bomb.isExploding = true;
                    bomb.explosionStartTime = TimeUtils.nanoTime();  // Record explosion start time
                }
            }
        }

        game.batch.end();

        drawHPBar();
        drawEnemyHPBar();


        if (Gdx.input.isKeyPressed(Keys.LEFT))
            player.setX(player.getX() - 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            player.setX(player.getX() + 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.UP))
            player.setY(player.getY() + 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            player.setY(player.getY() - 200 * Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyPressed(Keys.A)){
            doDamage();
            currentPlayerStance = playerAttackStance;
        } else {
            currentPlayerStance = playerNormalStance;
        }
        if (Gdx.input.isKeyPressed(Keys.E) && currentTime - lastBombDropTime >= cooldownTime) {
            // Create a new bomb
            Rectangle bombRect = new Rectangle(player.getX(), player.getY(), 64, 64);
            Bomb bomb = new Bomb(bombRect);
            bombs.add(bomb);
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


    private void doDamage(){
        for (Enemy enemy : enemies){
            if(player.getPlayerRect().overlaps(enemy.getRect())){
                // this is being reused. Will make a function
                if (TimeUtils.timeSinceMillis(lastDamageTimePlayer) >= damageIntervalPlayer) {
                    enemy.takeDamage(20); // Player takes 10 damage every 2 seconds
                    lastDamageTimePlayer = TimeUtils.millis(); // Update the time when the last damage was taken
                }
            }
        }

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
            if (player.getPlayerRect().overlaps(enemy.getRect())) {
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
            float currentHPWidth = (enemy.getHealth() / (float)enemy.getMaxHP()) * hpBarWidth;
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

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    class Bomb {
        Rectangle rect;
        long spawnTime;
        boolean isExploding = false;  // Whether the bomb is in the exploding state
        long explosionStartTime;  // Time when the explosion started (for timing)
        float explosionRadius = 100f;

        public Bomb(Rectangle rect) {
            this.rect = rect;
            this.spawnTime = TimeUtils.nanoTime(); // Capture when the bomb was placed
        }
    }
}
