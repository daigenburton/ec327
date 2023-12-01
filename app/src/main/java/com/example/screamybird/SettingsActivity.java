package com.example.screamybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {

    SeekBar seekBar, seekBarVolume, seekBarMusic;

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //get max and current volume
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);


        seekBarVolume = findViewById(R.id.volslider);
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        seekBarMusic = findViewById(R.id.musicslider);


    }
}