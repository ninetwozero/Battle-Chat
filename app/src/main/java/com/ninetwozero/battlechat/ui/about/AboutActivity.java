/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

package com.ninetwozero.battlechat.ui.about;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ninetwozero.battlechat.R;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends FragmentActivity implements ActionBar.TabListener {
    private final int[] TITLES = new int[] {
        R.string.title_about_app,
        R.string.title_about_author,
        R.string.title_about_collaborators,
        R.string.title_about_open_source
    };

    private ViewPager viewPager;
    private AboutFragmentAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initialize();
    }

    @Override
    public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    private void initialize() {
        setupViewPagerAdapter();
        setupViewPager();
        setupActionBar();
    }

    private void setupViewPagerAdapter() {
        viewPagerAdapter = new AboutFragmentAdapter(getSupportFragmentManager(), generateFragmentList());
    }

    private List<Fragment> generateFragmentList() {
        final List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(AppInfoFragment.newInstance());
        fragments.add(AuthorInfoFragment.newInstance());
        fragments.add(CollaboratorInfoFragment.newInstance());
        return fragments;
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    getActionBar().setSelectedNavigationItem(position);
                }
            }
        );
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0, max = TITLES.length; i < max; i++) {
            actionBar.addTab(actionBar.newTab().setText(TITLES[i]).setTabListener(this));
        }
    }
}
