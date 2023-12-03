package com.example.screamybird;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;


public class SettingsActivity2 extends AppCompatActivity {
    private int currentBrightness = 0;
    private Window window;
    private ContentResolver contentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        askPermission(this);

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //get max and current volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar seekBarVolume = findViewById(R.id.volslider);
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        //slider for system brightness
        window = getWindow();
        contentResolver = this.getContentResolver();

        SeekBar seekBarBrightness = findViewById(R.id.brightnessslider);
        seekBarBrightness.setMax(255);
        seekBarBrightness.setMin(0);
        seekBarBrightness.setKeyProgressIncrement(1);

        try {
            currentBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            seekBarBrightness.setProgress(currentBrightness);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentBrightness = progress;
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, currentBrightness);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = currentBrightness / (float) 300;
                window.setAttributes(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //back button implementation
        ImageButton backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    public void askPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                //you have permissions
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                startActivity(intent);
            }
        }
    }
}