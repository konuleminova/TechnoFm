package app.android.technofm.oidarfm.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import app.android.technofm.oidarfm.R;
import app.android.technofm.oidarfm.service.BackgroundSoundService;

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
    AsyncTask task;

    @Override
    public void startActivity(Intent intent) {
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fm, container, false);
        intent = new Intent(getActivity(), BackgroundSoundService.class);
        initViews(view);
        if (BackgroundSoundService.player != null) {
            if (BackgroundSoundService.player.isPlaying()) {
                pauseVisible();
            } else {
                playVisible();
            }
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(getActivity())) {
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
                    task.execute();


                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                }
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
}

