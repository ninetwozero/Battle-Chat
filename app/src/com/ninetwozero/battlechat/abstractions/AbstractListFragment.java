package com.ninetwozero.battlechat.abstractions;

import android.app.ListFragment;

import com.ninetwozero.battlechat.interfaces.ActivityAccessInterface;

public abstract class AbstractListFragment extends ListFragment {
    protected void showToast(final int resource) {
        showToast(getActivity().getString(resource));
    }

    protected void showToast(final String message) {
        ((ActivityAccessInterface) getActivity()).showToast(message);
    }

    protected void logout() {
        ((ActivityAccessInterface) getActivity()).logoutFromWebsite();
    }
}
