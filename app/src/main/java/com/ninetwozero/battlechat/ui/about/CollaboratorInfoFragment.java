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

import android.os.Bundle;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.HeaderAboutRow;
import com.ninetwozero.battlechat.datatypes.SimpleAboutRow;

import java.util.ArrayList;
import java.util.List;

public class CollaboratorInfoFragment extends BaseAboutFragment {

    public static CollaboratorInfoFragment newInstance() {
        final CollaboratorInfoFragment fragment = new CollaboratorInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public CollaboratorInfoFragment() {

    }

    @Override
    protected List<HeaderAboutRow> getHeadersForList() {
        final List<HeaderAboutRow> items = new ArrayList<HeaderAboutRow>();
        items.add(new HeaderAboutRow(R.string.collab_label_code, 1));
        items.add(new HeaderAboutRow(R.string.collab_label_design, 2));
        items.add(new HeaderAboutRow(R.string.collab_label_misc, 4));
        return items;
    }

    @Override
    protected List<SimpleAboutRow> getItemsForList() {
        final List<SimpleAboutRow> items = new ArrayList<SimpleAboutRow>();
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_peter,
                R.string.collab_desc_peter,
                "http://peterscorner.co.uk"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_humphreybc,
                R.string.collab_desc_humphreybc,
                "http://humphreybc.com"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_dbagjones,
                R.string.collab_desc_dbagjones,
                "https://www.twitter.com/dbagjones"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_lindyhop,
                R.string.collab_desc_lindyhop,
                "http://xanderapps.com"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_stillesjo,
                R.string.collab_desc_stillesjo,
                "http://stillesjo.org"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_dapil,
                R.string.collab_desc_dapil,
                "http://www.danielpeukert.cz/"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.collab_name_gk,
                R.string.collab_desc_gk,
                "http://www.gustavkarlsson.se"
            )
        );
        return items;
    }
}
