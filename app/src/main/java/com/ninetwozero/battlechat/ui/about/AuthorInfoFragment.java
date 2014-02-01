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
import com.ninetwozero.battlechat.interfaces.AboutInfoRow;

import java.util.ArrayList;
import java.util.List;

public class AuthorInfoFragment extends BaseAboutListFragment {

    public static AuthorInfoFragment newInstance() {
        final AuthorInfoFragment fragment = new AuthorInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public AuthorInfoFragment() {

    }

    @Override
    protected List<AboutInfoRow> getItemsForList() {
        final List<AboutInfoRow> items = new ArrayList<AboutInfoRow>();
        items.add(new AboutInfoRow(R.string.text_about_author_heading_whoami));
        items.add(
            new AboutInfoRow(
                R.string.text_about_author_name,
                R.string.text_about_author_job_title,
                "http://linkedin.com/in/ninetwozero"
            )
        );
        items.add(new AboutInfoRow(R.string.text_about_author_heading_contact));
        items.add(
            new AboutInfoRow(
                R.string.web,
                R.string.text_about_author_visit_website,
                "http://www.ninetwozero.com"
            )
        );
        items.add(
            new AboutInfoRow(
                R.string.account_email,
                R.string.text_about_author_send_email,
                "mailto:support@ninetwozero.com"
            )
        );
        items.add(
            new AboutInfoRow(
                R.string.account_twitter,
                R.string.text_about_author_tweet_twitter,
                "https://www.twitter.com/karllindmark"
            )
        );
        items.add(
            new AboutInfoRow(
                R.string.account_googleplus,
                R.string.text_about_author_circle_gplus,
                "https://plus.google.com/+KarlLindmark"
            )
        );
        return items;
    }
}
