/*
 *
 * 	This file is part of BattleChat
 *
 * 	BattleChat is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	BattleChat is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * /
 */

package com.ninetwozero.battlechat.ui.about;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninetwozero.battlechat.R;

public class AppInfoFragment extends Fragment {

    public static AppInfoFragment newInstance() {
        final AppInfoFragment fragment = new AppInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public AppInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        setupVersion(view);
        return view;
    }

    private void setupVersion(final View view) {
        final Activity activity = getActivity();
        String versionNumber = "Unknown";

        if (activity != null) {
            try {
                final PackageManager manager = getActivity().getPackageManager();
                if (manager != null) {
                    versionNumber = manager.getPackageInfo(activity.getPackageName(), 0).versionName;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((TextView) view.findViewById(R.id.version)).setText(versionNumber);
    }
}
