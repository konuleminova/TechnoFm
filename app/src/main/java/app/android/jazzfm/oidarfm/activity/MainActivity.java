package app.android.jazzfm.oidarfm.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import app.android.jazzfm.oidarfm.service.BackgroundSoundService;
import app.android.jazzfm.oidarfm.adapter.PageAdapter;
import app.android.jazzfm.oidarfm.R;
import app.android.jazzfm.oidarfm.service.StopServiceReceiver;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout fmInfoBar;

    TextView fmBar, infoBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabLayout.Tab infoTab, fmTab;

    @Override
    protected void onPause() {
        super.onPause();
        notificationMethod();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, BackgroundSoundService.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTabs();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                initViews();
                if (viewPager.getCurrentItem() == 1) {
                    fmTab.setText(getResources().getString(R.string.info));
                    infoTab.setText(getResources().getString(R.string.fm));
                    setInfoBar();

                } else if (viewPager.getCurrentItem() == 0) {
                    setFmBar();
                }
            }

            @Override
            public void onPageSelected(int position) {

                initViews();
                if (viewPager.getCurrentItem() == 1) {
                    setInfoBar();
                } else if (viewPager.getCurrentItem() == 0) {
                    setFmBar();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initViews() {
        fmBar = (TextView) fmInfoBar.findViewById(R.id.fm_textView);
        infoBar = (TextView) fmInfoBar.findViewById(R.id.info_TextView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void notificationMethod() {

        int icon = R.drawable.noticon;
        int mNotificationId = 001;
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                MainActivity.this);
        final Notification notification;
        Intent sintent = new Intent(this, StopServiceReceiver.class);
        PendingIntent ssIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, sintent, 0);
        mBuilder.setDeleteIntent(ssIntent);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notifiaction);
        if ((BackgroundSoundService.player == null) || (!BackgroundSoundService.player.isPlaying())) {
            contentView.setImageViewResource(R.id.play_imageView, R.drawable.iconpause);
            notification = mBuilder.setSmallIcon(icon).setTicker(getResources().getString(R.string.jazfm)).setWhen(0)

                    .setContentIntent(resultPendingIntent)
                    .setDeleteIntent(ssIntent)
                    .setContent(contentView)
                    .build();

        } else {

            contentView.setImageViewResource(R.id.play_imageView, R.drawable.iconplay);
            notification = mBuilder.setSmallIcon(icon).setTicker(getResources().getString(R.string.jazfm)).setWhen(0)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setDeleteIntent(ssIntent)
                    .setContent(contentView)
                    .build();

        }
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);

    }

    public void setTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tablelayout);
        fmTab = tabLayout.newTab();
        fmTab.setText(getResources().getString(R.string.fm));
        tabLayout.addTab(fmTab);
        tabLayout.setSelectedTabIndicatorColor(0xffff);
        fmInfoBar = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.fm_info_bar, null);
        tabLayout.getTabAt(0).setCustomView(fmInfoBar);
        infoTab = tabLayout.newTab();
        infoTab.setText(getResources().getString(R.string.info));
        tabLayout.addTab(infoTab);
        viewPager = (ViewPager) findViewById(R.id.page);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        fmTab.setText(getResources().getString(R.string.info));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void setFmBar() {
        tabLayout.getTabAt(0).setCustomView(fmInfoBar);
        tabLayout.getTabAt(1).setCustomView(null);
        fmBar.setVisibility(View.VISIBLE);
        infoBar.setVisibility(View.INVISIBLE);
    }

    private void setInfoBar() {
        tabLayout.getTabAt(0).setCustomView(null);
        tabLayout.getTabAt(1).setCustomView(fmInfoBar);
        fmBar.setVisibility(View.INVISIBLE);
        infoBar.setVisibility(View.VISIBLE);
    }
}








