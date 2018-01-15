package app.android.technofm.oidarfm.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import app.android.technofm.oidarfm.R;
import app.android.technofm.oidarfm.activity.MainActivity;
import app.android.technofm.oidarfm.service.BackgroundSoundService;

import static android.content.ContentValues.TAG;

/**
 * Created by Asus on 10/24/2017.
 */

public class FmTab extends Fragment {
    private AdView mAdView;
    Button pauseButton;
    Button playButton;
    Intent intent;
    TextView plsWaitTextView;
    ImageView plswaitImageView;
    static AsyncTask task;
    int delay = 500;
    Thread timer;

    @Override
    public void startActivity(Intent intent) {
        getActivity().startService(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fm, container, false);
        intent = new Intent(getActivity(), BackgroundSoundService.class);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "I am the UI thread");

                initViews(view);

                if (BackgroundSoundService.player != null) {
                    if (BackgroundSoundService.player.isPlaying()) {
                        pauseVisible();
                    } else {
                        playVisible();
                    }
                }
            }
        });



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backgroundTimer();


            }

        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(getActivity())) {

                    if (BackgroundSoundService.player != null) {
                        BackgroundSoundService.player.pause();
                    } else {
                        getActivity().stopService(new Intent(getActivity(), BackgroundSoundService.class));
                    }
                    playVisible();
                } else {
                    playVisible();
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAdView = (AdView) view.findViewById(R.id.adView);
        setmAdView();
        return view;
    }

    private void initViews(View view) {
        playButton = (Button) view.findViewById(R.id.btn_play);
        pauseButton = (Button) view.findViewById(R.id.btn_pause);
        plsWaitTextView = (TextView) view.findViewById(R.id.plswait);
        plswaitImageView = (ImageView) view.findViewById(R.id.imgview);
    }

    public void setmAdView() {

        MobileAds.initialize(getContext(), getResources().getString(R.string.addcontext));
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(getResources().getString(R.string.adddevice))
                .build();
        mAdView.loadAd(request);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void playVisible() {
        pauseButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
    }

    public void pauseVisible() {
        pauseButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.INVISIBLE);
    }

    public void gonePlswaitMethod() {
        plsWaitTextView.setVisibility(View.GONE);
        plswaitImageView.setVisibility(View.INVISIBLE);
    }

    public void visiblePlswaitMethod() {
        plsWaitTextView.setVisibility(View.VISIBLE);
        plswaitImageView.setVisibility(View.VISIBLE);
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
                              getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Log.d("UI thread", "I am the UI thread");
                                        doAscyn();
                                        task.execute();
                                    }
                                });


                            }
                        };
                        timer.schedule(hourlyTask, delay);

                    }
                });

            }
        });
        timer.start();

    }

    public void doAscyn() {
        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                if (isNetworkAvailable(getActivity()) == true) {

                    if (BackgroundSoundService.player != null) {

                        BackgroundSoundService.player.start();


                    } else {
                        getActivity().startService(new Intent(getActivity(), BackgroundSoundService.class));
                    }


                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_LONG).show();
                }

                return objects;
            }

            @Override
            protected void onPreExecute() {
                playButton.setVisibility(View.INVISIBLE);
                visiblePlswaitMethod();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                if (BackgroundSoundService.player != null) {
                    if (BackgroundSoundService.player.isPlaying()) {
                        pauseVisible();
                    } else {
                        playVisible();
                    }
                }
                pauseVisible();
                gonePlswaitMethod();
                super.onPostExecute(o);
            }
        };

    }
}
