package com.ninetwozero.battlechat.abstractions;

import android.app.Activity;
import android.app.ListFragment;

import com.ninetwozero.battlechat.interfaces.ActivityAccessInterface;

public abstract class AbstractListFragment extends ListFragment {
    protected void showToast(final int resource) {
        final Activity activity = getActivity();
        if( activity != null ) {
            showToast(activity.getString(resource));
        }
    }

    protected void showToast(final String message) {
        final ActivityAccessInterface activity = (ActivityAccessInterface) getActivity();
        if( activity != null ) {
            activity.showToast(message);
        }
    }

    protected void logout() {
        final ActivityAccessInterface activity = (ActivityAccessInterface) getActivity();
        if( activity != null ) {
            activity.logoutFromWebsite();
        }
    }
}
