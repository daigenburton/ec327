package com.example.screamybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Random;


public class GameView extends SurfaceView implements Runnable{
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;

    private Paint paint;
    private Snake[] snakes;
    private Random random;
    private Slime slime;
    private Background background1, background2;
    public GameView(Context context, int screenX, int screenY) {
        super(context);

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

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(slime.getDead(), slime.x, slime.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            for (Snake snake : snakes) {
                canvas.drawBitmap(snake.getSnake(), snake.x, snake.y, paint);
            }
            canvas.drawBitmap(slime.getSlime(), slime.x, slime.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
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
