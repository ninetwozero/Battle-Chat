package com.ninetwozero.battlechat.base.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseListFragment extends ListFragment {
    private Toast toast;
    protected LayoutInflater layoutInflater;
    protected SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.layoutInflater = inflater;
        return null;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    protected void showToast(final int resource) {
        final Activity activity = getActivity();
        if( activity != null ) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(activity, resource, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void showToast(final String message) {
        final Activity activity = getActivity();
        if( activity != null ) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
