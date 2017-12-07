package app.android.jazzfm.oidarfm.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import app.android.jazzfm.oidarfm.R;

public class BackgroundSoundService extends Service {

    private static final String TAG = null;
    public static MediaPlayer player;
    int delay = 900000;
    Thread timer;
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            player = new MediaPlayer();
            player = MediaPlayer.create(this, Uri.parse(getResources().getString(R.string.stream)));
            player.prepare();
            player.setLooping(true);
            player.setVolume(100, 100);
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "IllegalArgumentException: " + e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {
        player.start();
        backgroundTimer();
        return START_STICKY;
    }

    public void backgroundTimer() {

        timer = new Thread(new Runnable() {

            @Override
            public void run() {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Timer timer = new Timer();
                        TimerTask hourlyTask = new TimerTask() {
                            @Override
                            public void run() {
                                player.pause();

                            }
                        };
                        timer.schedule(hourlyTask, delay);

                    }
                });

            }
        });
        timer.start();

    }


}
