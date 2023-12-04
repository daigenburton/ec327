package com.example.screamybird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import java.util.Random;

/*
This class is the game view and it is where the game is drawn and updated. It preforms many of the calculations for the game,
such as hitbox collision and voice input. It also handles the game over screen and the score.
 */
public class GameView extends SurfaceView implements Runnable {
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
    public Slime slime;
    private NewGameActivity activity;
    private Background background1, background2;
    private Context context;

    //For audio input
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final long MOVE_UP_COOLDOWN = 1000;
    private boolean canMoveUp = true;
    private long lastMoveUpTime = 0;

    private AudioRecord audioRecord;
    private short[] audioBuffer;

    // Constructor
    public GameView(NewGameActivity activity, int screenX, int screenY) {
        super(activity);
        this.activity = activity;
        preferences = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        // Make it usable for different build versions
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

        // Makes it work on every device
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

        for (int i = 0; i < 4; i++) {
            Snake snake = new Snake(getResources());
            snakes[i] = snake;
        }
        random = new Random();
    }


    @SuppressLint("MissingPermission") // Permission is checked in MainActivity
    // Initializes the audio recorder
    private void initAudioRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioBuffer = new short[bufferSize];


        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );
    }

    // Starts the audio recorder, and starts a new thread for processing the audio data
    private void startRecording() {
        audioRecord.startRecording();

        // Start a separate thread for processing the audio data
        new Thread(this::processAudio).start();
    }

    // Stops the audio recorder
    private void stopRecording() {
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }
    }

    // Processes the audio data and determines if the slime should move up
    private void processAudio() {
        while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            int bytesRead = audioRecord.read(audioBuffer, 0, audioBuffer.length);
            if (bytesRead > 0) {
                int sum = 0;
                for (int i = 0; i < bytesRead; i++) {
                    sum += audioBuffer[i] * audioBuffer[i];
                }
                double amplitude = sum / (double) bytesRead;
                if (amplitude > 200000 && canMoveUp) {
                    slime.isGoingUp = true;
                    canMoveUp = false;
                    lastMoveUpTime = System.currentTimeMillis();
                }
                if (!canMoveUp && System.currentTimeMillis() - lastMoveUpTime > MOVE_UP_COOLDOWN) {
                    slime.isGoingUp = false;
                    canMoveUp = true;
                }
            }
        }
    }

    // Runs the game
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    // Updates the game, makes sure that snakes continue to move and that the slime moves up and down
    private void update() {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

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
        if (slime.y > screenY - (slime.height + 300)) {
            slime.y = screenY - (slime.height + 300);
        }

        if (!preferences.getBoolean("isMute", false)) {
            soundPool.play(sound, 1, 1, 0, -1, 1);
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
                    snake.y = random.nextInt(screenY - snake.height + 300);
                }
            }


            if (Rect.intersects(snake.GetCollisionShape(), slime.GetCollisionShape())) {
                //GAME OVER
                isGameOver = true;
                return;
            }
        }
    }

    // Draws the game, draws the background, snakes, and slime
    private void draw() {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Snake snake : snakes) {
                canvas.drawBitmap(snake.getSnake(), snake.x, snake.y, paint);
            }

            canvas.drawText(score + "", screenX / 2f, 150, paint);
            if (isGameOver) {
                isPlaying = false;

                canvas.drawBitmap(slime.getDead(), slime.x, slime.y, paint);
                getHolder().unlockCanvasAndPost(canvas);

                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(slime.getSlime(), slime.x, slime.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }


    }

    // Waits 2 seconds before exiting the game
    private void waitBeforeExiting() {
        try {
            Thread.sleep(2000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Saves the high score
    private void saveIfHighScore() {
        if (preferences.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    // Makes the game sleep for 17 milliseconds
    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Resumes the game, starts the audio recorder
    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        initAudioRecorder();
        startRecording();
    }

    // Pauses the game, stops the audio recorder and clears it so that it can be used again
    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopRecording();
        audioRecord.release();
    }

    // Handles the touch event, makes the slime move up when the left side of the screen is touched
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





