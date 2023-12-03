package com.example.screamybird;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameThread extends Thread{
    private Game game;
    private boolean isPlaying;
    private int fr; //frame rate
    private Thread thread;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;

    private Paint paint;
    private Background background1, background2;
    private Player player;
    private Bitmap bmp;
    Rect screen;
    public GameThread (Game game) {
        this.game = game;
    }


    /*
    * This will probably change
    * if u need to change anything here go ahead
    */



    public void run() {
        long lastTime = System.currentTimeMillis();
        while (isPlaying) {
                long now = System.currentTimeMillis();
                long elapsed = now - lastTime;

                if (elapsed < fr) {
                    game.update(elapsed); //need to add update function to game
                    //game.draw(); //need to add draw
                }
                lastTime = now;
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
    }

    private void draw () {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint );

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
}

    public void shutdown() {
        isPlaying = false;
    }


}

