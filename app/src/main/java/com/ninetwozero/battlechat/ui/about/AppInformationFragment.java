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

public class AppInformationFragment extends Fragment {

    public static AppInformationFragment newInstance() {
        final AppInformationFragment fragment = new AppInformationFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public AppInformationFragment() {

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

    /*private void setupLink(final View view) {
        view.findViewById(R.id.link).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.ninetwozero.com")));
                }
            }
        );
        view.findViewById(R.id.email).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND).putExtra(
                        Intent.EXTRA_EMAIL,
                        new String[]{"support@ninetwozero.com"}
                    ).setType("plain/text");
                    startActivity(Intent.createChooser(intent, "Send an e-mail..."));
                }
            }
        );
    }*/
}
