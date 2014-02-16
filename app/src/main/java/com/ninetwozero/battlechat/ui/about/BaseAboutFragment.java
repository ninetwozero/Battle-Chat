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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.HeaderAboutRow;
import com.ninetwozero.battlechat.datatypes.SimpleAboutRow;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.List;

public abstract class BaseAboutFragment
    extends Fragment
    implements StickyGridHeadersGridView.OnItemClickListener
{
    private StickyGridHeadersGridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onItemClick(final AdapterView<?> av, final View v, final int position, final long id) {
        final SimpleAboutRow row = getListAdapter().getItem(position);
        if (row.getType() == SimpleAboutRow.Type.HEADER || row.getUrl() == null) {
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
        setupGridView(view);
        setListAdapter(new AboutInfoAdapter(getActivity(), getHeadersForList(), getItemsForList()));
    }

    private void setupGridView(final View view) {
        gridView = (StickyGridHeadersGridView) view.findViewById(android.R.id.list);
        gridView.setAreHeadersSticky(false);
    }

    protected abstract List<HeaderAboutRow> getHeadersForList();
    protected abstract List<SimpleAboutRow> getItemsForList();

    public AboutInfoAdapter getListAdapter() {
        return (AboutInfoAdapter) gridView.getAdapter();
    }

    public void setListAdapter(final AboutInfoAdapter listAdapter) {
        gridView.setAdapter(listAdapter);
    }
}
