package com.ninetwozero.battlechat.ui.about;

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
 *
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.interfaces.AboutInfoRow;

import java.util.List;

public abstract class BaseAboutListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.generic_list, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final AboutInfoRow row = ((AboutInfoAdapter) getListAdapter()).getItem(position);
        if (row.getType() == AboutInfoRow.Type.HEADER || row.getUrl() == null) {
            return;
        }

        startActivity(
            Intent.createChooser(
                new Intent(Intent.ACTION_VIEW).setData(Uri.parse(row.getUrl())),
                "Select an app"
            )
        );
    }

    private void initialize(final View view) {
        setListAdapter(new AboutInfoAdapter(getActivity(), getItemsForList()));
    }

    protected abstract List<AboutInfoRow> getItemsForList();
}
