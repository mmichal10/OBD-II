package com.example.michal.inz.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ParamsFragment paramsFragment;
    private MapsFragment mapsFragment;
    private SettingsFragment settingsFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        paramsFragment = new ParamsFragment();
        mapsFragment = new MapsFragment();
        settingsFragment = new SettingsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return paramsFragment;
            case 1:
                return mapsFragment;
            case 2:
                return settingsFragment;
        }

        return paramsFragment;
       /*
        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragment " + position);

        paramsFragment.setArguments(bundle);
        */
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        FragmentName fragment;
        String name = "Error name";
        try {
            fragment = (FragmentName) this.getItem(position);
            name = fragment.getName();
        } catch (Exception e) {
            Log.e("PagerAdapter", e.getMessage());
        }

        return name;
    }
}
