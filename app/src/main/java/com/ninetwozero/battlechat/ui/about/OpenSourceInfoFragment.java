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

public class OpenSourceInfoFragment extends BaseAboutFragment {

    public static OpenSourceInfoFragment newInstance() {
        final OpenSourceInfoFragment fragment = new OpenSourceInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public OpenSourceInfoFragment() {

    }

    @Override
    protected List<HeaderAboutRow> getHeadersForList() {
        final List<HeaderAboutRow> items = new ArrayList<HeaderAboutRow>();
        items.add(new HeaderAboutRow(R.string.opensource_label_self, 1));
        items.add(new HeaderAboutRow(R.string.opensource_label_libs, 5));
        return items;
    }

    @Override
    protected List<SimpleAboutRow> getItemsForList() {
        final List<SimpleAboutRow> items = new ArrayList<SimpleAboutRow>();
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_self,
                R.string.opensource_subtitle_self,
                "https://github.com/ninetwozero/Battle-Chat"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_gson,
                R.string.opensource_subtitle_gson,
                "https://code.google.com/p/google-gson"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_httprequest,
                R.string.opensource_subtitle_httprequest,
                "https://github.com/kevinsawicki/http-request"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_jsoup,
                R.string.opensource_subtitle_jsoup,
                "http://jsoup.org/"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_otto,
                R.string.opensource_subtitle_otto,
                "http://square.github.io/otto/"
            )
        );
        items.add(
            new SimpleAboutRow(
                R.string.opensource_title_picasso,
                R.string.opensource_subtitle_picasso,
                "http://square.github.io/picasso/"
            )
        );
        return items;
    }
}
