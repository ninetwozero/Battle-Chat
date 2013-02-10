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
	
	final public void setItems(List<T> items) {
		mItems = items;
		notifyDataSetChanged();
	}
	
	final public void setText(View container, int resourceId, Object text) {
		((TextView) container.findViewById(resourceId)).setText(String.valueOf(text));	
	}

	@Override
	abstract public long getItemId(int position);

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
}
