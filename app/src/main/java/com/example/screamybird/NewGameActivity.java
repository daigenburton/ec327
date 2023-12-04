package com.example.screamybird;

import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


//this is the game activity that runs the game, it switches from the main activity to this activity when the game starts
public class NewGameActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //creates the game
        super.onCreate(savedInstanceState);


        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        gameView = new GameView(this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {  //pauses the game
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() { //resumes the game
        super.onResume();
        gameView.resume();
    }

}

