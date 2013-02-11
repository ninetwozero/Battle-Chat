package com.ninetwozero.battlechat.adapters;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListAdapter;
import com.ninetwozero.battlechat.datatypes.Message;

public class MessageListAdapter extends AbstractListAdapter<Message> {
	
	private String mOtherUser;
	
	public MessageListAdapter(Context context, String other) {
		super(context);
		mOtherUser = other;
	}
	
	public MessageListAdapter(Context context, List<Message> items, String other) {
		super(context, items);
		mOtherUser = other;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message message = getItem(position);
		final boolean fromOtherUser = mOtherUser.equals(message.getUsername());
		if( convertView == null ) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_message, null);
		}
		
		setText(convertView, R.id.username, message.getUsername(), fromOtherUser? R.color.orange : R.color.blue);
		setText(convertView, R.id.message, message.getMessage());
		
		/* TODO: Need a "n seconds/minutes/hours/days ago" */
		setText(convertView, R.id.timestamp, DateUtils.getRelativeTimeSpanString(message.getTimestamp(), System.currentTimeMillis()/1000, 0));
		
		return convertView;
	}

}
