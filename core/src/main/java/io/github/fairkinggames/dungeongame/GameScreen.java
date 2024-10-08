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
    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    int dropsGathered;

    // Game Assets
    Texture backgroundImage;
    Texture rockImage;
    Texture treeImage;
    Texture warriorImage;
    Texture enemyImage;
    Rectangle rock;
    Rectangle tree;
    Rectangle player;
    Rectangle enemy;

    // list of rocks and trees to be created. List<GameObject> obstacles;

    Array<Rectangle> obstacles;
    Array<Rectangle> enemies;

    int playerMaxHp = 100;
    int playerCurrentHp = 100;
    int enemyMaxHp = 100;
    int enemyCurrentHp = 100;

    long lastDamageTimeEnemy = 0;
    long lastDamageTimePlayer = 0;
    long damageIntervalEnemy = 2000;
    long damageIntervalPlayer = 1000;
    float hpBarWidth = 64;
    float hpBarHeight = 10;

    // To draw HP bar
    ShapeRenderer shapeRenderer;


    public GameScreen(final Dungeon game) {
        this.game = game;

        shapeRenderer = new ShapeRenderer();

        obstacles = new Array<Rectangle>();
        enemies = new Array<Rectangle>();

        // To be removed
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the images for the background, rock, tree, and player class
        //backgroundImage = new Texture(Gdx.files.internal("ph_bgyellow.png"));
        rockImage = new Texture(Gdx.files.internal("ph_rock.png"));
        treeImage = new Texture(Gdx.files.internal("ph_tree.png"));
        warriorImage = new Texture(Gdx.files.internal("ph_war.png"));
        enemyImage = new Texture(Gdx.files.internal("ph_enemy.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create a Rectangle to logically represent the player
        player = new Rectangle();
        player.x = 1280 / 2 - 64 / 2; // center the player horizontally
        player.y = 20; // bottom left corner of the player is 20 pixels above
        // the bottom screen edge
        player.width = 64;
        player.height = 64;

        // create a Rectangle to logically represent the tree
        tree = new Rectangle();
        tree.x = 32; // top left tree
        tree.y = 32;
        // the bottom screen edge
        tree.width = 64;
        tree.height = 64;
        obstacles.add(tree);

        // create a Rectangle to logically represent the rock
        rock = new Rectangle();
        rock.x = 1280 / 2 - 64 / 2; // center the rock
        rock.y = 720 /2 - 64 / 2;
        // the bottom screen edge
        rock.width = 64;
        rock.height = 64;
        obstacles.add(rock);

        enemy = new Rectangle();
        enemy.x = 100;
        enemy.y = 100;
        enemy.width = 64;
        enemy.height = 64;
        enemies.add(enemy);

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

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.font.draw(game.batch, "Your HP: " + playerCurrentHp, 0, 480);
        game.font.draw(game.batch, "Enemy HP: " + enemyCurrentHp, 0, 380);
        game.batch.draw(warriorImage, player.x, player.y, player.width, player.height);

        for (Rectangle obstacle : obstacles) {
            if (obstacle == rock){
                game.batch.draw(rockImage, obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }
            else if (obstacle == tree){
                game.batch.draw(treeImage, obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            }
        }

        //Rename this E later, enemy is not a good name, should likely be types of enemies.
        for (Rectangle E : enemies) {
            if (E == enemy){
                game.batch.draw(enemyImage, enemy.x, enemy.y, enemy.width, enemy.height);
            }
        }

        game.batch.end();

        drawHPBar();
        drawEnemyHPBar();

        // to be removed as the game is not going to be drag to move
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            player.x = touchPos.x - 64 / 2;
            player.y = touchPos.y - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            player.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            player.x += 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.UP))
            player.y += 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            player.y -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.A))
            doDamage();

        checkCollision();
        checkDamage();


        // make sure the bucket stays within the screen bounds
        if (player.x < 0)
            player.x = 0;
        if (player.x > 1280 - 64)
            player.x = 1280 - 64;

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
        dropImage.dispose();
        bucketImage.dispose();
        warriorImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

    private void checkCollision(){
        for (Rectangle obstacle : obstacles) {
            if (player.overlaps(obstacle)) {
                movePlayerBack();
            }
        }
    }

    private void doDamage(){
        if(player.overlaps(enemy)){
            // this is being reused. Will make a function
            if (TimeUtils.timeSinceMillis(lastDamageTimePlayer) >= damageIntervalPlayer) {
                enemyTakeDamage(20); // Player takes 10 damage every 2 seconds
                lastDamageTimePlayer = TimeUtils.millis(); // Update the time when the last damage was taken
            }
        }

    }

    private void checkDamage(){
        for (Rectangle E : enemies) {
            if (player.overlaps(E)) {
                movePlayerBack();
                if (TimeUtils.timeSinceMillis(lastDamageTimeEnemy) >= damageIntervalEnemy) {
                    playerTakeDamage(10); // Player takes 10 damage every 2 seconds
                    lastDamageTimeEnemy = TimeUtils.millis(); // Update the time when the last damage was taken
                }
            }
        }
    }

    private void movePlayerBack() {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.x += 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.y -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.y += 200 * Gdx.graphics.getDeltaTime();
        }
    }

    private void drawHPBar() {
        // Calculate the width of the HP bar based on the player's current HP
        float currentHPWidth = (playerCurrentHp / (float)playerMaxHp) * hpBarWidth;

        // Set the color and draw the HP bar above the player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background of the HP bar (gray)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player.x, player.y + player.height + 10, hpBarWidth, hpBarHeight);

        // Draw the actual HP bar (red)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(player.x, player.y + player.height + 10, currentHPWidth, hpBarHeight);

        shapeRenderer.end();
    }

    private void drawEnemyHPBar() {
        // Calculate the width of the HP bar based on the player's current HP
        float currentHPWidth = (enemyCurrentHp / (float)enemyMaxHp) * hpBarWidth;

        // Set the color and draw the HP bar above the player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background of the HP bar (gray)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 10, hpBarWidth, hpBarHeight);

        // Draw the actual HP bar (red)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(enemy.x, enemy.y + enemy.height + 10, currentHPWidth, hpBarHeight);

        shapeRenderer.end();
    }


    public void playerTakeDamage(int damage) {
        playerCurrentHp -= damage;
        if (playerCurrentHp < 0) {
            playerCurrentHp = 0; // Player's health shouldn't go below 0
        }
    }

    public void enemyTakeDamage(int damage) {
        enemyCurrentHp -= damage;
        if (enemyCurrentHp < 0) {
            enemyCurrentHp = 0; // Player's health shouldn't go below 0
        }
    }

}
