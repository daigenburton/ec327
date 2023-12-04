package com.example.screamybird;


import static com.example.screamybird.GameView.screenRatioX;
import static com.example.screamybird.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
//this is the slime class that creates the slime and animates it
public class Slime {

    boolean isGoingUp = false;
    int x, y, width, height, slimeCounter=0;
    Bitmap slime1, slime2, dead;

    Slime (int screenY, Resources res) {
        slime1 = BitmapFactory.decodeResource(res, R.drawable.slime1);
        slime2 = BitmapFactory.decodeResource(res, R.drawable.slime2);

        width = slime1.getWidth();
        height = slime1.getHeight();

        width /= 15;
        height /= 15;

        width *= (int) (width * screenRatioX);
        height *= (int) (height * screenRatioY);

        slime1 = Bitmap.createScaledBitmap(slime1, width, height, false);
        slime2 = Bitmap.createScaledBitmap(slime2, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y =  screenY/2;
        x = (int) (64 * screenRatioX);
    }

    Bitmap getSlime () {
        if (slimeCounter == 0) {
            slimeCounter++;
            return slime1;
        }
        slimeCounter--;
        return slime2;
    } //this animates the slime with slime1 and 2

    Rect GetCollisionShape () {
        return new Rect(x, y, (int) (x + width*.6), (int) (y + height*.75));
    }

    Bitmap getDead () {
        return dead;
    }
}
