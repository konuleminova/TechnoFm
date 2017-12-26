package app.android.technofm.oidarfm.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.android.technofm.oidarfm.fragment.FmTab;
import app.android.technofm.oidarfm.fragment.InfoTab;

/**
 * Created by Asus on 10/16/2017.
 */

public class PageAdapter extends FragmentStatePagerAdapter {
    private int numberOfTabs;

    public PageAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FmTab();
            case 1:
                InfoTab info = new InfoTab();
                return info;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}