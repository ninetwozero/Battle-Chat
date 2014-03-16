package com.ninetwozero.battlechat.base.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.ui.adapters.ViewPagerAdapter;

import java.util.List;

public abstract class BaseTabActivity extends BaseActivity implements ActionBar.TabListener {
    public static final String INTENT_EXTRA = "extra";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_multi_tab_activity);
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
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), generateFragmentList());
    }

    private List<Fragment> generateFragmentList() {
        final Bundle dataBundle = getBundleFromIntent(getIntent());
        return fetchFragmentsForActivity(dataBundle);
    }

    private Bundle getBundleFromIntent(final Intent intent) {
        return intent.hasExtra(INTENT_EXTRA) ? intent.getBundleExtra(INTENT_EXTRA) : new Bundle();
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(getOffscreenPageLimit());
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
        final int[] titles = getTitleResources();
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0, max = titles.length; i < max; i++) {
            actionBar.addTab(actionBar.newTab().setText(titles[i]).setTabListener(this));
        }
    }

    protected abstract int[] getTitleResources();
    protected abstract List<Fragment> fetchFragmentsForActivity(final Bundle profileBundle);
    protected abstract int getOffscreenPageLimit();
}
