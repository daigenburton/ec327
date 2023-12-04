package com.example.screamybird;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;
import android.view.WindowInsets;

import java.io.IOException;


public class MainActivity extends NewGameActivity {

    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 0x00;

    private int volumeThreshold;

    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For API level 30 and above
            View decorView = getWindow().getDecorView();
            decorView.setOnApplyWindowInsetsListener((v, insets) -> {
                WindowInsetsController insetsController = v.getWindowInsetsController();
                if (insetsController != null) {
                    insetsController.hide(WindowInsets.Type.statusBars());
                    insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
                return insets.consumeSystemWindowInsets();
            });
        } else {
            // For API level 29 and below
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        // Get the volume threshold
        SharedPreferences settings = getPreferences(0);
        volumeThreshold = settings.getInt("VolumeThreshold", 50);


        //settings menu and implementation
        Button settingsbutton = findViewById(R.id.settings);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Switch Activities on click
                Intent intent = new Intent(MainActivity.this,
                        SettingsActivity2.class);
                startActivity(intent);
            }
        });

        //check if play is clicked and create new intent to start the game activity
        Button playbutton = findViewById(R.id.play);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                // Switch Activities on click

                Intent intent = new Intent(MainActivity.this, NewGameActivity.class);
                startActivity(intent);
            }
        });
    }


    public void goToVoiceActivity(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, NewGameActivity.class); //back-end file for game play
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted
                    Intent intent = new Intent(this, NewGameActivity.class);
                    intent.putExtra("Mode", "Voice");
                    startActivity(intent);
                } else {
                    // Permission denied
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Permission Denied...");
                    alertDialog.setMessage("Without record audio permission granted, " +
                            "you cannot play with voice control.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                        }
                    });
                    alertDialog.show();
                }
                return;
            }
        }
    }

}