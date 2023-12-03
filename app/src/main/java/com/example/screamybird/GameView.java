package com.example.screamybird;

import android.content.Context;
<<<<<<< HEAD
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
    private Thread thread;
    private boolean isPlaying;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
=======
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder holder;
    GameThread thread;
    Game game;
>>>>>>> 770d3a3d8f47d6112b01c2f7504387a2cf733938

    private Paint paint;
    private Background background1, background2;
    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;

        //makes it work on every device
        screenRatioX = 1920f / screenX;
        screenRatioY = 2220f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.x = screenX;

        paint = new Paint();
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
    }
    private void draw () {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint );

            getHolder().unlockCanvasAndPost(canvas);


        }
    }

    private void sleep () {
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

    public void pause () {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
