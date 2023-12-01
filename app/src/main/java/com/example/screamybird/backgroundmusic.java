package com.example.screamybird;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class backgroundmusic extends Service {
    private MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setVolume(100, 100);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_STICKY;
    }
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    };
}
