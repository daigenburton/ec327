package com.example.screamybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {
    int x =0,y=0;
    protected Bitmap background;

    Background (int screenX, int screenY, Resources res) {  //creates the scrolling background
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX,screenY, false);
    }
}
