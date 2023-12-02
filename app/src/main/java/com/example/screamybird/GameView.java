/* TODO
*   - Add update function to keep game running
*    - Figure out collisions and how to keep player running on terrain
*    - Add jumping behavior
*    - Handle player dying and game resetting
*/

package com.example.screamybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatDrawableManager;

import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.screamybird.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder surfaceHolder;
    Paint paint;
    Bitmap bitmap;
    private float measuredWidth;
    private float measuredHeight;


    //Player values
    private float positionX = 0.0f;
    private float positionY = 0.0f;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float accelerationX = 0.0f;
    private float accelerationY = 0.7f;

    // Terrain values
    private int iteratorInt = 0;
    private static final int interval = 150;
    private static final float gap = 300.0f;
    private static final float base = 500.0f;
    private float terrainWidth = 500.0f;
    private List<Terrain> terrainList;
    private static final float terrainVelocity = 3.0f;



    public GameView (Context context) {
        super (context);

        init();
    }
    public GameView (Context context, AttributeSet setA) {
        super (context, setA);

        init();
    }

    public GameView (Context context, AttributeSet setA, int B) {
        super (context, setA, B);

        init();
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


    /*
    * Adds a new piece of terrain to the list to be used
    */
    void addTerrain () {
        terrainList.add(new Terrain(measuredWidth + terrainWidth / 2.0f,
                base + (measuredHeight - 2 * base - gap) * new Random().nextFloat()));
    }








    /*
    *Sets position of player
    */
    public void setPosition (float xPos, float yPos) {
        this.positionX = xPos;
        this.positionY = yPos;
    }

    /*
    * Function initalizes the player's model and the terrain
    */
    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        paint = new Paint();

        // For player
        bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.idle);
        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

        // Array for terrain generation
        terrainList = new ArrayList<Terrain>();

        setKeepScreenOn(true);
    }


    /*
    * Creates a bitmap from context
    */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);

        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /*
    * Resets all values, used for restarts
    */
    public void reset () {
        //Player values reset to 0
        positionX = 0.0f;
        positionY = 0.0f;
        velocityX = 0.0f;
        velocityY = 0.0f;
        accelerationX = 0.0f;
        accelerationY = 0.7f;

        //Resets Terrain generation
        iteratorInt = 0;
        terrainList = new ArrayList<>(); //Creates new list to store terrain pieces


        setPosition(measuredWidth/2, measuredHeight/2);

        addTerrain();
    }



}
