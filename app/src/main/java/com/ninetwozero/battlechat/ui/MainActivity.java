package com.ninetwozero.battlechat.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseFragmentActivity;
import com.ninetwozero.battlechat.datatypes.ToggleNavigationDrawerRequest;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.datatypes.UserLogoutEvent;
import com.ninetwozero.battlechat.services.BattleChatService;
import com.ninetwozero.battlechat.ui.about.AboutActivity;
import com.ninetwozero.battlechat.ui.chat.ChatFragment;
import com.ninetwozero.battlechat.ui.fragments.StartupFragment;
import com.ninetwozero.battlechat.ui.navigation.NavigationDrawerFragment;
import com.ninetwozero.battlechat.ui.settings.SettingsActivity;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity
    extends BaseFragmentActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String TAG = "MainActivity";

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_DRAWER_OPENED = "isDrawerOpened";

    private Timer timer;
    private boolean userLearnedDrawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View fragmentContainerView;
    private NavigationDrawerFragment navigationDrawer;
    private boolean shouldShowDualPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        startTimer();
        showNotification();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTimer();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleNavigationDrawer(!isDrawerOpen());
                return true;
            case R.id.menu_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            case R.id.menu_refresh:
                BusProvider.getInstance().post(new TriggerRefreshEvent(TriggerRefreshEvent.Type.MANUAL));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menu_exit:
                triggerLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void triggerLogout() {
        startService(
            BattleChatService.getIntent(getApplicationContext()).putExtra(
                BattleChatService.INTENT_CALLED_FROM_ACTIVITY, true
            ).putExtra(
                BattleChatService.INTENT_ACTION, BattleChatService.ACTION_LOGOUT
            )
        );
    }

    @Override
    public void onBackPressed() {
        if (!shouldShowDualPane) {
            if (isDrawerOpen()) {
                toggleNavigationDrawer(false);
            } else {
                navigationDrawer.getListView().setItemChecked(-1, true);
            }
        }

        setActionBarText(R.string.title_main, true);
        final FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(StartupFragment.TAG) == null) {
            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(
                R.id.content_root,
                StartupFragment.newInstance(),
                StartupFragment.TAG
            );
            transaction.commit();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_DRAWER_OPENED, isDrawerOpen() || shouldShowDualPane);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    @Override
    public void onNavigationDrawerItemSelected(final String title) {
        this.title = title == null ? this.title : title;
        this.subtitle = null;

        if (drawerLayout != null) {
            toggleNavigationDrawer(false);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(final String title, final String subtitle) {
        this.title = title == null ? this.title : title;
        this.subtitle = subtitle == null ? this.subtitle : subtitle;

        if (drawerLayout != null) {
            toggleNavigationDrawer(false);
        }
    }

    @Subscribe
    public void onRequestToToggleTheNavigationDrawer(final ToggleNavigationDrawerRequest request) {
        toggleNavigationDrawer(request.isShouldOpen());
    }

    private void initialize(final Bundle savedInstanceState) {
        loadSettingsFromXml();
        setupInitialFragment();
        if (shouldShowDualPane) {
            setupDualPaneLayout(savedInstanceState);
        } else {
            setupNavigationDrawer();
            setupActionBar();
            setupActionBarToggle();
        }
        setupActivityFromState(savedInstanceState);
    }

    private void loadSettingsFromXml() {
        shouldShowDualPane = getResources().getBoolean(R.bool.main_is_dualpane);
    }

    private void setupDualPaneLayout(final Bundle savedInstanceState) {
        if (findViewById(R.id.friends_list) != null) {
            // TODO: Set up the menu???
        }
    }

    private void setupNavigationDrawer() {
        navigationDrawer = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentByTag(
            NavigationDrawerFragment.TAG
        );
    }

    private void setupActionBarToggle() {
        userLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        fragmentContainerView = findViewById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerToggle = new ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.drawable.ic_navigation_drawer,
            R.string.label_friends,
            R.string.app_name
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (!navigationDrawer.isAdded()) {
                    return;
                }

                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    sharedPreferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
            }
        };

        if (!userLearnedDrawer) {
            drawerLayout.openDrawer(fragmentContainerView);
        }

        drawerLayout.post(
            new Runnable() {
                @Override
                public void run() {
                    drawerToggle.syncState();
                }
            }
        );
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setupActivityFromState(final Bundle state) {
        if (state != null) {
            if (!shouldShowDualPane) {
                toggleNavigationDrawer(state.getBoolean(STATE_DRAWER_OPENED, false));
            }
        }
    }

    private void toggleNavigationDrawer(final boolean show) {
        if (show) {
            drawerLayout.openDrawer(fragmentContainerView);
            navigationDrawer.setMenuVisibility(true);
        } else {
            drawerLayout.closeDrawer(fragmentContainerView);
            navigationDrawer.setMenuVisibility(false);
        }
    }

    private void setupInitialFragment() {
        final FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(ChatFragment.TAG) == null) {
            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_root, StartupFragment.newInstance(), StartupFragment.TAG);
            transaction.commit();
            setActionBarText(R.string.title_main, true);
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    BusProvider.getInstance().post(
                                        new TriggerRefreshEvent(TriggerRefreshEvent.Type.AUTOMATIC)
                                    );
                                }
                            }
                        );
                    }
                },
                0,
                sharedPreferences.getInt(Keys.Settings.CHAT_INTERVAL, 25) * 1000 //--> ms
            );
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Subscribe
    public void onReceivedLogoutEvent(final UserLogoutEvent event) {
        Log.d(TAG, "Received logout event: " + event);
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
