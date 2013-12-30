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

package com.ninetwozero.battlechat.ui.chat;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseListAdapter;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.Message;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageListAdapter extends BaseListAdapter<Message> {

    private String self;
    private User user;

    public MessageListAdapter(final Context context, final String self, final List<Message> items) {
        super(context);
        this.self = self;
        this.items = items;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(final int position) {
        if (getItem(position).getUsername().equalsIgnoreCase(self)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Message message = getItem(position);
        final boolean ownMessage = self.equals(message.getUsername());
        if (convertView == null) {
            convertView = layoutInflater.inflate(fetchLayoutResource(position), null);
        }

        displayAvatar(convertView, ownMessage);
        setText(convertView, R.id.message, String.valueOf(Html.fromHtml(message.getMessage())));
        setText(convertView, R.id.timestamp, DateTimeUtils.toRelative(context, message.getTimestamp()));
        return convertView;
    }

    private void displayAvatar(final View view, final boolean isOwnMessage) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
        if (imageView != null) {
            if (user == null) {
                imageView.setImageResource(R.drawable.default_gravatar);
            } else {
                Picasso.with(context).load(fetchGravatarUrl(isOwnMessage)).into(imageView);
            }
        }
    }

    private String fetchGravatarUrl(boolean ownMessage) {
        if (ownMessage) {
            return Session.getGravatarUrl();
        } else {
            return UrlFactory.buildGravatarUrl(user.getGravatarHash());
        }
    }

    private int fetchLayoutResource(final int position) {
        if ( getItemViewType(position) == 0) {
            return R.layout.list_item_message_right;
        } else {
            return R.layout.list_item_message_left;
        }
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
