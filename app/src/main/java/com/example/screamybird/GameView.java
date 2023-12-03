package com.example.screamybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
    private boolean isPlaying;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;

    private Paint paint;
    private Background background1, background2;
    private Player player;
    private Bitmap bmp;
    Rect screen;

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

        //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.green_idle1);


        /*player = new Player(this.bmp, context, new Rect(
                400,
                screen.height()/2,
                520,
                screen.height()/2 + 220),
                screen); */
    }






