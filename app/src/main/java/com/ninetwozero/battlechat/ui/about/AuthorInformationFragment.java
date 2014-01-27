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
import com.ninetwozero.battlechat.interfaces.AuthorInfoRow;

import java.util.ArrayList;
import java.util.List;

public class AuthorInformationFragment extends ListFragment {

    public static AuthorInformationFragment newInstance() {
        final AuthorInformationFragment fragment = new AuthorInformationFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public AuthorInformationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_about_author, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final AuthorInfoRow row = ((AuthorInformationAdapter) getListAdapter()).getItem(position);
        if (row.getType() == AuthorInfoRow.Type.HEADER) {
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
        setListAdapter(new AuthorInformationAdapter(getActivity(), getItemsForList()));
    }

    private List<AuthorInfoRow> getItemsForList() {
        final List<AuthorInfoRow> items = new ArrayList<AuthorInfoRow>();
        items.add(new AuthorInfoRow(R.string.text_about_author_heading_whoami));
        items.add(
            new AuthorInfoRow(
                R.string.text_about_author_name,
                R.string.text_about_author_job_title,
                "http://linkedin.com/in/ninetwozero"
            )
        );
        items.add(new AuthorInfoRow(R.string.text_about_author_heading_contact));
        items.add(
            new AuthorInfoRow(R.string.web,
                R.string.text_about_author_visit_website,
                "http://www.ninetwozero.com"
            )
        );
        items.add(
            new AuthorInfoRow(R.string.account_email,
                R.string.text_about_author_send_email,
                "mailto:support@ninetwozero.com"
            )
        );
        items.add(
            new AuthorInfoRow(R.string.account_twitter,
                R.string.text_about_author_tweet_twitter,
                "https://www.twitter.com/karllindmark"
            )
        );
        items.add(
            new AuthorInfoRow(
                R.string.account_googleplus,
                R.string.text_about_author_circle_gplus,
                "https://plus.google.com/+KarlLindmark"
            )
        );
        return items;
    }
}
