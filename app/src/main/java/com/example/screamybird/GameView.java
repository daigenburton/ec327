package com.example.screamybird;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Random;

//this class is the game view and it is where the game is drawn and updated
public class GameView extends SurfaceView implements Runnable{
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Snake[] snakes;
    private SharedPreferences preferences;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private Slime slime;
    private NewGameActivity activity;
    private Background background1, background2;
    public GameView(NewGameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;


    preferences = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(activity, R.raw.music, 1);

        this.screenX = screenX;
        this.screenY = screenY;

        //makes it work on every device
        screenRatioX = 1080f / screenX;
        screenRatioY = 2220f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        slime = new Slime(screenY, getResources());

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        snakes = new Snake[4];

        for (int i =0; i < 4; i++) {
            Snake snake = new Snake(getResources());
            snakes[i] = snake;
        }
        random = new Random();
    }
    @Override
    public void run() {
        while (isPlaying) {
            update ();
            draw ();
            sleep ();
        }
    }

    private void update () {
        if (!preferences.getBoolean("isMute", false)) {
            soundPool.play(sound, 1, 1, 0, -1, 1);
        }

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 *screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }
        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }
        if (slime.isGoingUp) {
            slime.y -= 30 * screenRatioY;
        } else {
            slime.y += 30 * screenRatioY;
        }
        if (slime.y < 0) {
            slime.y = 0;
        }
        if (slime.y > screenY - (slime.height+300)) {
            slime.y = screenY - (slime.height+300);
        }

        for (Snake snake : snakes) {

            snake.x -= snake.speed;

            if (snake.x + snake.width < 0) {
                int bound = (int) (30 * screenRatioX);
                snake.speed = random.nextInt(bound);

                if (snake.speed < 10 * screenRatioX) {
                    snake.speed = (int) (10 * screenRatioX);
                    score++;
                    snake.x = screenX;
                    snake.y = random.nextInt(screenY - snake.height+300);
                }

            }

            if (Rect.intersects(snake.GetCollisionShape(), slime.GetCollisionShape())) {
                //GAME OVER
                isGameOver = true;
                return;
            }
        }
    }
    private void draw () {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint );

            for (Snake snake : snakes) {
                canvas.drawBitmap(snake.getSnake(), snake.x, snake.y, paint);
            }

            canvas.drawText(score + "", screenX / 2f, 150, paint);
            if (isGameOver) {
                isPlaying = false;
                getHolder().unlockCanvasAndPost(canvas);

                canvas.drawBitmap(slime.getDead(), slime.x, slime.y, paint);
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(slime.getSlime(), slime.x, slime.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }



    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(2000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if (preferences.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2) {
                    slime.isGoingUp = true;
                }
                break;
                case MotionEvent.ACTION_UP:
                    slime.isGoingUp = false;
                    break;
        }
        return true;
    }
}
