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

package com.ninetwozero.battlechat.abstractions;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class AbstractListAdapter<T extends Object> extends BaseAdapter {
	protected final static String TAG = "AbstractListAdapter";
	
	protected Context mContext;
	protected List<T> mItems;
	protected final LayoutInflater mLayoutInflater;
		
	public AbstractListAdapter(Context context) {
		mContext = context;
		mItems = new ArrayList<T>();
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public AbstractListAdapter(Context context, List<T> items) {	
		mContext = context;
		mItems = items;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	final public int getCount() {
		return mItems == null? 0 : mItems.size();
	}

	@Override
	final public T getItem(int position) {
		return mItems.get(position);
	}
	
	final public List<T> getItems() {
		return mItems;
	}
	
	final public void setItems(List<T> items) {
		mItems = items;
		notifyDataSetChanged();
	}
	
	final public void setText(View container, int resourceId, Object text) {
		setText(container, resourceId, text, -1);
	}
	
	final public void setText(View container, int resourceId, Object text, int colorResource) {
		TextView textView = (TextView) container.findViewById(resourceId);
		textView.setText(String.valueOf(text));	
		if( colorResource > -1 ){
			textView.setTextColor(mContext.getResources().getColor(colorResource));
		}
	}

	@Override
	abstract public long getItemId(int position);

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
}
