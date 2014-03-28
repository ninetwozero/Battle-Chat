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

package com.ninetwozero.battlechat.base.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.QueryResult;

public abstract class BaseCursorListAdapter<T extends QueryResult> extends BaseAdapter {
    private static final int NO_COLOR = -1;

    protected final Context context;
    protected CursorList<T> items;
    protected final LayoutInflater layoutInflater;

    public BaseCursorListAdapter(final Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public BaseCursorListAdapter(final Context context, final CursorList<T> items) {
        this.context = context;
        this.items = items;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    final public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    final public T getItem(int position) {
        return items.get(position);
    }

    final public CursorList<T> getItems() {
        return items;
    }

    final public void setItems(CursorList<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    final public void setText(final View container, final int resourceId, final String text) {
        setText(container, resourceId, text, NO_COLOR);
    }

    final public void setText(final View container, final int resourceId, final String text, final int colorResource) {
        TextView textView = (TextView) container.findViewById(resourceId);
        textView.setText(text);
        if (colorResource > NO_COLOR) {
            textView.setTextColor(context.getResources().getColorStateList(colorResource));
        }
    }

    final public void setText(final View container, final int resourceId, final int text) {
        setText(container, resourceId, text, NO_COLOR);
    }

    final public void setText(final View container, final int resourceId, final int text, final int colorResource) {
        TextView textView = (TextView) container.findViewById(resourceId);
        textView.setText(text);
        if (colorResource > NO_COLOR) {
            textView.setTextColor(context.getResources().getColorStateList(colorResource));
        }
    }

    final public void setImage(final View container, final int resourceId, final int drawable) {
        ((ImageView) container.findViewById(resourceId)).setImageResource(drawable);
    }

    @Override
    abstract public long getItemId(int position);

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);
}
