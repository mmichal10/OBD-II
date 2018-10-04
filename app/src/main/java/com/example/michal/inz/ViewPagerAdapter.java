package com.example.michal.inz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ParamsFragment paramsFragment = new ParamsFragment();

        position = position + 1;

        Bundle bundle = new Bundle();
        bundle.putString("message", "Fragment " + position);

        paramsFragment.setArguments(bundle);

        return paramsFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        position += 1;

        return "Fragment " + position;
    }
}
