package com.example.arpit.myapplication.fragmentTabular;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments =  new ArrayList<>();
    ArrayList<String> tabTitle = new ArrayList<>();

    public void addFragments(Fragment fragment , String tabTiles){
        this.fragments.add(fragment);
        this.tabTitle.add(tabTiles);
    }
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle.get(position);
    }
}
