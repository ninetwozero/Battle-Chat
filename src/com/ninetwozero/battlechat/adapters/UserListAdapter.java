package com.ninetwozero.battlechat.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListAdapter;
import com.ninetwozero.battlechat.datatypes.User;

public class UserListAdapter extends AbstractListAdapter<User> {
	private final int HEADING = 0;
	private final int ITEM = 1;
	
	public UserListAdapter(Context context) {
		super(context);
	}
	
	public UserListAdapter(Context context, List<User> items) {
		super(context, items);
	}
	
	@Override
	public boolean isEnabled(int position) {
		return getItem(position).getId() > 0;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mItems.get(position).getId() == 0? HEADING : ITEM;
	
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final User user = getItem(position);
		if( getItemViewType(position) == HEADING ) {
			if( convertView == null ) {
				convertView = mLayoutInflater.inflate(R.layout.list_item_heading, null);
			}
			setText(convertView, R.id.title, user.getUsername());
			
		} else {
			if( convertView == null ) {
				convertView = mLayoutInflater.inflate(R.layout.list_item_user, null);
			}
		
			setText(convertView, R.id.username, user.getUsername(), getColorForStatus(user.isPlaying(), user.isOnline()));
			setText(convertView, R.id.status, user.getOnlineStatus(), getColorForStatus(user.isPlaying(), user.isOnline()));
			convertView.setTag(user);
		}
		return convertView;
	}

	private int getColorForStatus(boolean playing, boolean online) {
		if( playing ) {
			return R.color.blue;
		} else if( online ) {
			return R.color.green;
		} else {
			return R.color.grey;
		}
	}

}
