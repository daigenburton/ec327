package com.example.screamybird;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.screamybird.GameActivity.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private GameView GameView;
    private TextView textViewScore;

    private boolean isGameOver;

    private boolean isSetNewTimerThreadEnabled;

    private int volumeThreshold;

    private Thread setNewTimerThread;

    private AlertDialog.Builder alertDialog;

    private MediaPlayer mediaPlayer;

    private int gameMode;

    private AudioRecorder audioRecorder;

    private final int TOUCH_MODE = 0;
    private final int VOICE_MODE = 1;
    // The what values of the messages
    protected final int UPDATE = 0;
    private final int RESET_SCORE = 1;
    private Timer timer;

    class handler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE: {
                    if (GameView.isAlive()) {
                        isGameOver = false;
                        GameView.update();
                    } else {
                        if (isGameOver) {
                            break;
                        } else {
                            isGameOver = true;
                        }

                        if (gameMode == TOUCH_MODE) {
                            // Cancel the timer
                            timer.cancel();
                            timer.purge();
                        } else {
                            audioRecorder.isGetVoiceRun = false;
                            audioRecorder = null;
                            System.gc();
                        }

                        alertDialog = new AlertDialog.Builder(GameActivity.this);
                        alertDialog.setTitle("GAME OVER");
                        alertDialog.setMessage("Score: " + String.valueOf(GameView.getScore()) +
                                "\n" + "Would you like to RESTART?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GameActivity.this.restartGame();
                            }
                        });
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GameActivity.this.onBackPressed();
                            }
                        });
                        alertDialog.show();
                    }

                    break;
                }

                case RESET_SCORE: {
                    textViewScore.setText("0");

                    break;
                }

                default: {
                    break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.level);

        // Get the mode of the game from the StartingActivity
        if (getIntent().getStringExtra("Mode").equals("Touch")) {
            gameMode = TOUCH_MODE;
        } else {
            gameMode = VOICE_MODE;

            volumeThreshold = getIntent().getIntExtra("VolumeThreshold", 50);
        }

        // Set the Timer
        isSetNewTimerThreadEnabled = true;
        setNewTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds for the Surface to initialize
                    Thread.sleep(3000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    if (isSetNewTimerThreadEnabled) {
                        setNewTimer();
                    }
                }
            }
        });
        setNewTimerThread.start();
        audioRecorder = new AudioRecorder();
        audioRecorder.getNoiseLevel();
    }

    protected class AudioRecorder {

        private static final String TAG = "AudioRecord";

        int SAMPLE_RATE_IN_HZ = 8000;

        int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);

        AudioRecord mAudioRecord;

        boolean isGetVoiceRun;

        Object mLock;

        public AudioRecorder() {
            mLock = new Object();
        }

        public void getNoiseLevel() {
            if (isGetVoiceRun) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
            if (mAudioRecord == null) {
                Log.e(TAG, "mAudioRecord initialization failed.");
            }
            isGetVoiceRun = true;

            new Thread(new Runnable() {

                @Override
                public void run() {
                    mAudioRecord.startRecording();
                    short[] buffer = new short[BUFFER_SIZE];
                    while (isGetVoiceRun) {
                        // r是实际读取的数据长度，一般而言r会小于buffersize
                        int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                        long v = 0;
                        // 将 buffer 内容取出，进行平方和运算
                        for (int i = 0; i < buffer.length; i++) {
                            v += buffer[i] * buffer[i];
                        }
                        // 平方和除以数据总长度，得到音量大小。
                        double mean = v / (double) r;
                        double volume = 10 * Math.log10(mean);
                        Log.i(TAG, "分贝值:" + volume);

                        // Jump if the volume is loud enough
                        if (volume > volumeThreshold) {
                            GameView.jump();
                            Log.i(TAG, "分贝值: " + volume + "超过了");
                        }

                        synchronized (mLock) {
                            try {
                                mLock.wait(17);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;
                }

            }).start();
        }

        /**
         * Sets the Timer to update the UI of the GameView.
         */
        private void setNewTimer() {
            if (!isSetNewTimerThreadEnabled) {
                return;
            }

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    // Send the message to the handler to update the UI of the GameView
                    GameActivity.handler.sendMessage(UPDATE);

                    // For garbage collection
                    System.gc();
                }

            }, 0, 17);
        }

        @Override
        protected void onDestroy() {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

            if (audioRecorder != null) {
                audioRecorder.isGetVoiceRun = false;
                audioRecorder = null;
            }

            isSetNewTimerThreadEnabled = false;

            super.onDestroy();
        }



        protected void onRestart() {

        }

        /**
         * Updates the displayed score.
         *
         * @param score The new score.
         */
        public void updateScore(int score) {
            textViewScore.setText(String.valueOf(score));
        }

        /**
         * Restarts the game.
         */
        private void restartGame() {
            // Reset all the data of the over game in the GameView
            GameView.reset();

            // Refresh the TextView for displaying the score
            new Thread(new Runnable() {

                @Override
                public void run() {
                    handler.sendEmptyMessage(RESET_SCORE);
                }

            }).start();

            audioRecorder = new AudioRecorder();
            audioRecorder.getNoiseLevel();
        }

        @Override
        public void onBackPressed() {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

            isSetNewTimerThreadEnabled = false;

            super.onBackPressed();
        }
    }
}

