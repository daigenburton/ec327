package com.example.screamybird;

import android.Manifest;
import android.app.GameState;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Game {

    private enum gameStates {
        START, LOST, RUNNING;
    }

    private Context context;
    private SurfaceHolder holder;
    private Rect screen;
    private Resources resources;
    private gameStates state = gameStates.START;

    private Player player;
    private Terrain terrain;
    private static final int RECORDER_BPP = 16;
    private static int RECORDER_SAMPLERATE = 8000;
    private static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    Paint borderPaint = new Paint();
    BitmapFactory.Options options;

    public Game(Context context, Rect screen, SurfaceHolder holder, Resources resources) {
        this.context = context;
        this.screen = screen;
        this.holder = holder;
        this.resources = resources;
        options = new BitmapFactory.Options();
        options.inScaled = false;
        reset();
    }

    public void onTouchEvent(MotionEvent event) {
        if(state == gameStates.LOST){
            reset();
        } else if(state == gameStates.START) {
            state = gameStates.RUNNING;
        }
    }

    private AudioRecord audioRecorder;
    private boolean isPlayerJumping = false;

    // Existing methods...

    public void update(Long elapsed) {
        if (state == gameStates.RUNNING) {
            recordAndAnalyzeSound();
        }
    }

    private void recordAndAnalyzeSound() {
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING);

        if (audioRecorder == null) {
            audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING,
                    bufferSizeInBytes);
            audioRecorder.startRecording();
        }

        int numberOfReadBytes = 0;
        byte audioBuffer[] = new byte[bufferSizeInBytes];
        float tempFloatBuffer[] = new float[3];
        int tempIndex = 0;

        float totalAbsValue = 0.0f;
        short sample = 0;

        numberOfReadBytes = audioRecorder.read(audioBuffer, 0, bufferSizeInBytes);

        // Analyze Sound.
        for (int i = 0; i < bufferSizeInBytes; i += 2) {
            sample = (short) ((audioBuffer[i]) | audioBuffer[i + 1] << 8);
            totalAbsValue += Math.abs(sample) / (numberOfReadBytes / 2);
        }

        // Analyze temp buffer.
        tempFloatBuffer[tempIndex % 3] = totalAbsValue;
        float temp = 0.0f;
        for (int i = 0; i < 3; ++i)
            temp += tempFloatBuffer[i];

        if ((temp >= 0 && temp <= 350) && !isPlayerJumping) {
            tempIndex++;
            return;
        }

        if (temp > 350 && !isPlayerJumping) {
            isPlayerJumping = true;
        }

        if ((temp >= 0 && temp <= 350) && isPlayerJumping) {
            player.jump();
            isPlayerJumping = false;
        }

        tempIndex++;
    }

    private void loseGame() {
        state = gameStates.LOST;
    }

    private void reset () {

    }



}

