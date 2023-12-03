package com.example.screamybird;

import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;

public class NewGameActivity extends AppCompatActivity {
    private GameView gameView;
    private Game game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        game.onTouchEvent(event);
        return true;
    }
}

