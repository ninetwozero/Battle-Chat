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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.interfaces.AuthorInfoRow;

import java.util.List;

public class AuthorInformationAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<AuthorInfoRow> items;

    public AuthorInformationAdapter(final Context context, final List<AuthorInfoRow> items) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public AuthorInfoRow getItem(final int position) {
        return items == null ? null : items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getType() == AuthorInfoRow.Type.HEADER) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return AuthorInfoRow.Type.values().length;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final AuthorInfoRow row = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(fetchLayoutResource(row.getType()), parent, false);
        }

        if (row.getType() == AuthorInfoRow.Type.HEADER) {
            ((TextView) convertView.findViewById(R.id.text1)).setText(row.getTitle());
        } else {
            ((TextView) convertView.findViewById(R.id.title)).setText(row.getTitle());
            ((TextView) convertView.findViewById(R.id.subtitle)).setText(row.getSubTitle());
        }
        return convertView;
    }

    private int fetchLayoutResource(final AuthorInfoRow.Type type) {
        if (type == AuthorInfoRow.Type.HEADER) {
            return R.layout.list_item_heading;
        } else {
            return R.layout.list_item_two_line_card;
        }
    }
}
