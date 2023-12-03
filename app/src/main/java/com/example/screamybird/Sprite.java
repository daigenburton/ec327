package com.example.screamybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/*
 * Parent sprite class, can be used for enemies and obstacles
 */
public class Sprite {

    public enum SpriteState{
        JUMPING, IDLE;
    }
    public Bitmap image;
    public Context context;
    private Rect hitbox;
    public Rect screen;
    private SpriteState spriteState;

    private int width;
    private int height;
    private double x;
    private double y;

    public double vx;
    public double vy;
    public double ax;
    public double ay;

    public final double GRAV = 4;
    public boolean affectedByGrav = false;

    Paint noAliasPaint = new Paint();
    Paint borderPaint = new Paint();
    Paint vectorPaint = new Paint();

    public Sprite (Bitmap image,Context context, Rect hitbox, Rect screen) {
        this.image = image;
        this.context = context;
        this.hitbox = hitbox;
        this.screen = screen;
        this.spriteState = SpriteState.IDLE;

        this.width = hitbox.width();
        this.height = hitbox.height();

        this.x = hitbox.left;
        this.y = hitbox.top;

        this.vy = 0;
        this.vx = 0;
        this.ax = 0;
        this.ay = 0;

        noAliasPaint.setAntiAlias(false);
        noAliasPaint.setFilterBitmap(false);
        noAliasPaint.setDither(false);
        noAliasPaint.setColor(Color.GREEN);

        borderPaint.setStrokeWidth(10);
        borderPaint.setStyle(Paint.Style.STROKE);

        vectorPaint.setStyle(Paint.Style.STROKE);
        vectorPaint.setStrokeWidth(8);
        vectorPaint.setColor(Color.GREEN);
    }

    public double getX () {
        return x;
    }

    public double getY () {
        return y;
    }

    public int getHeight () {
        return height;
    }

    public int getWidth () {
        return width;
    }

    public double getRight () {
        return (this.getWidth() + this.x);
    }

    public double getBottom () {
        return (this.getHeight() + this.y);
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setX (double newX) {
        this.x = newX;

        this.getHitbox().set((int) x, (int) y, (int) getRight(), (int) getBottom());
    }

    public void setY(double y) {
        this.y = y;

        this.getHitbox().set((int) x, (int) y, (int) getRight(), (int) getBottom());
    }

    public void update() {
        vx += ax;
        vy += ay;

        if (this.affectedByGrav) {
            vy+= GRAV;
        }

        setX(this.getX() + vx);
        setY(this.getY() + vy);
    }

    public void drawHitbox (Canvas canvas, int color) {
        borderPaint.setColor(color);
        this.setY(this.getY());
        canvas.drawRect(hitbox, borderPaint);
    }

    //Might not need drawVecs class
    public void drawVecs (Canvas canvas, int scalar) {
        Path path = new Path();
        path.moveTo(this.getHitbox().centerX(),
                this.getHitbox().centerY());
        path.lineTo(this.getHitbox().centerX(),
                this.getHitbox().centerY());
        path.close();
        canvas.drawPath(path, vectorPaint);
    }

    public void draw (Canvas canvas, long elevation) {
        if(image != null) {
            this.setY(this.getY());
            canvas.drawBitmap(image, null, getHitbox(), null);
        } else {
            drawHitbox(canvas, Color.MAGENTA);
        }
    }
}

