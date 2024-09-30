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
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;

    // Game Assets
    Texture backgroundImage;
    Texture rockImage;
    Texture treeImage;
    Texture warriorImage;
    Rectangle rock;
    Rectangle tree;
    Rectangle player;

    // list of rocks and trees to be created. List<GameObject> obstacles;

    public GameScreen(final Dungeon game) {
        this.game = game;

        // To be removed
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the images for the background, rock, tree, and player class
        //backgroundImage = new Texture(Gdx.files.internal("ph_bgyellow.png"));
        rockImage = new Texture(Gdx.files.internal("ph_rock.png"));
        treeImage = new Texture(Gdx.files.internal("ph_tree.png"));
        warriorImage = new Texture(Gdx.files.internal("ph_war.png"));

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

        // create a Rectangle to logically represent the rock
        rock = new Rectangle();
        rock.x = 1280 / 2 - 64 / 2; // center the rock
        rock.y = 720 /2 - 64 / 2;
        // the bottom screen edge
        rock.width = 64;
        rock.height = 64;

        // create the raindrops array and spawn the first raindrop
        //raindrops = new Array<Rectangle>();
        //spawnRaindrop();

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(Color.BLACK);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(warriorImage, player.x, player.y, player.width, player.height);
        game.batch.draw(treeImage, tree.x, tree.y, tree.width, tree.height);
        game.batch.draw(rockImage, rock.x, rock.y, rock.width, rock.height);

        /* for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }*/
        game.batch.end();

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

}
