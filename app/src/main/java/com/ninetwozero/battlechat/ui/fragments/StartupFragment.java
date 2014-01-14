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

package com.ninetwozero.battlechat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.ToggleNavigationDrawerRequest;
import com.ninetwozero.battlechat.utils.BusProvider;

public class StartupFragment extends Fragment {
    public static final String TAG = "StartupFragment";

    public StartupFragment() {
    }

    public static Fragment newInstance() {
        final StartupFragment fragment = new StartupFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle icicle) {
        final View view = inflater.inflate(R.layout.fragment_startup, parent, false);
        initialize(view);
        return view;
    }

    private void initialize(final View view) {
        view.findViewById(R.id.startup_button).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post(new ToggleNavigationDrawerRequest(true));
                }
            }
        );
    }
}
