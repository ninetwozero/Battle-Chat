package com.ninetwozero.battlechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ninetwozero.battlechat.Keys;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseFragmentActivity;
import com.ninetwozero.battlechat.datatypes.UserLogoutEvent;
import com.ninetwozero.battlechat.ui.navigation.NavigationDrawerFragment;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

public class ShareActivity extends BaseFragmentActivity {
    public static final String TAG = "ShareToChatActivity";

    private NavigationDrawerFragment navigationDrawer;

    @Override
    protected void onStart() {
        super.onStart();

        checkForStoredSession();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initialize(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void getIntent() {

    }

    private void initialize(final Bundle savedInstanceState) {
        setupFriendList();
    }

    private void checkForStoredSession() {
        if (!alreadyHasCookie()) {
            startActivity(new Intent(this, ShareActivity.class));
            finish();
        }
    }

    private boolean alreadyHasCookie() {
        return sharedPreferences.contains(Keys.Session.COOKIE_VALUE) && !sharedPreferences.getString(Keys.Session.COOKIE_VALUE, "").equals("");
    }

    private void setupFriendList() {
    }

    @Subscribe
    public void onReceivedLogoutEvent(final UserLogoutEvent event) {
        Log.d(TAG, "Received logout event: " + event);
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
