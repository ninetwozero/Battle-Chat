package com.ninetwozero.battlechat.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.datatypes.Message;

public class MessageListAdapter extends AbstractListAdapter<Message> {
	
	public MessageListAdapter(Context context) {
		super(context);
	}
	
	public MessageListAdapter(Context context, List<Message> items) {
		super(context, items);
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_message, null);
		}
		return convertView;
	}

}