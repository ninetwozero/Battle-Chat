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
import com.ninetwozero.battlechat.datatypes.HeaderAboutRow;
import com.ninetwozero.battlechat.datatypes.SimpleAboutRow;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;

import java.util.List;

public class AboutInfoAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<HeaderAboutRow> headers;
    private List<SimpleAboutRow> items;

    public AboutInfoAdapter(final Context context, final List<HeaderAboutRow> headers, final List<SimpleAboutRow> items) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.headers = headers;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    public HeaderAboutRow getHeader(final int position) {
        return headers == null ? null : headers.get(position);
    }

    @Override
    public SimpleAboutRow getItem(final int position) {
        return items == null ? null : items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getCountForHeader(final int position) {
        return headers == null ? 0 : headers.get(position).getChildCount();
    }

    @Override
    public int getNumHeaders() {
        return headers == null ? 0 : headers.size();
    }

    @Override
    public View getHeaderView(final int position, View convertView, final ViewGroup parent) {
        final HeaderAboutRow row = getHeader(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_heading, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.text1)).setText(row.getTitle());
        return convertView;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final SimpleAboutRow row = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_two_line_card, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.title)).setText(row.getTitle());
        ((TextView) convertView.findViewById(R.id.subtitle)).setText(row.getSubTitle());
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
       return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).getType() == SimpleAboutRow.Type.ITEM;
    }
}
