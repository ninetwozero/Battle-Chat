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

package com.ninetwozero.battlechat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.ninetwozero.battlechat.R;

public class EulaFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "EulaFragment";
    public static final String USER_ACCEPTED_EULA = "has_user_accepted_eula";

    static EulaFragment newInstance() {
        final EulaFragment fragment = new EulaFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_launcher)
            .setTitle("Terms of use")
            .setPositiveButton(android.R.string.yes, this)
            .setNegativeButton(android.R.string.no, this)
            .setView(LayoutInflater.from(activity).inflate(R.layout.fragment_eula, null, false));
        return builder.create();
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        final Activity activity = getActivity();
        if (which == Dialog.BUTTON_POSITIVE) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(USER_ACCEPTED_EULA, true);
            editor.commit();

            dismiss();
        } else {
            activity.finish();
        }
    }
}
