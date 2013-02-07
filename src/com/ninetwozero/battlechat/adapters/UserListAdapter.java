package com.ninetwozero.battlechat.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.User;

public class UserListAdapter extends AbstractListAdapter<User> {
	
	public UserListAdapter(Context context) {
		super(context);
	}
	
	public UserListAdapter(Context context, List<User> items) {
		super(context, items);
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final User user = getItem(position);
		if( convertView == null ) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_user, null);
		}
		
		setText(convertView, R.id.username, user.getName());
		setText(convertView, R.id.status, user.getOnlineStatus());
		
		convertView.setTag(user);
		return convertView;
	}

}
