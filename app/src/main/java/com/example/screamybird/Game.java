package com.example.screamybird;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class Game {

    private enum gameStates {
        START, LOST, RUNNING;
    }

    private Context context;
    private SurfaceHolder holder;
    private Rect screen;
    private Resources resources;
    private gameStates state = gameStates.START;

    private Player player;
    private Terrain terrain;

    Paint borderPaint = new Paint();
    BitmapFactory.Options options;

    public Game(Context context, Rect screen, SurfaceHolder holder, Resources resources) {
        this.context = context;
        this.screen = screen;
        this.holder = holder;
        this.resources = resources;
        options = new BitmapFactory.Options();
        options.inScaled = false;
        reset();
    }

    public void reset() {

    }

    public void update(long elapsed) {

    }

    public void draw() {

    }
}

