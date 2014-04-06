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
import com.ninetwozero.battlechat.base.ui.BaseCursorListAdapter;
import com.ninetwozero.battlechat.dao.MessageDAO;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import se.emilsjolander.sprinkles.CursorList;

public class RegularChatListAdapter extends BaseCursorListAdapter<MessageDAO> {
    private final String self;
    private User user;

    public RegularChatListAdapter(final Context context, final String self, final CursorList<MessageDAO> items) {
        super(context);
        this.self = self;
        this.items = items;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getUsername().equals(self) ? 0 : 1;
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final MessageDAO message = getItem(position);
        final boolean ownMessageDAO = self.equals(message.getUsername());
        if (convertView == null) {
            convertView = layoutInflater.inflate(fetchLayoutResource(position), null);
        }

        displayAvatar(convertView, ownMessageDAO);
        setText(convertView, R.id.message, String.valueOf(Html.fromHtml(message.getContent())));
        setText(convertView, R.id.timestamp, DateTimeUtils.toRelative(message.getTimestamp()));
        return convertView;
    }

    /* TODO: Future stuff
    private void handleGroupChatSpecifics(final View view, MessageDAO message) {
        String contentToDisplay = "Unknown group chat event";
        if (message.getType() == Message.Type.INVITED_TO_GROUP) {
            contentToDisplay = String.format(
                context.getString(R.string.chat_group_invited),
                message.getExtra()
            );
        } else if (message.getType() == Message.Type.JOINED_GROUP) {
            contentToDisplay = String.format(
                context.getString(R.string.chat_group_joined),
                message.getExtra()
            );

        } else if (message.getType() == Message.Type.LEFT_GROUP) {
            contentToDisplay = String.format(
                context.getString(R.string.chat_group_left),
                message.getExtra()
            );

        } else if (message.getType() == Message.Type.JOINED_GAME) {
            final GroupJoinedGame extra = (GroupJoinedGame) message.getExtra();
            contentToDisplay = String.format(
                context.getString(R.string.chat_group_joinedgame),
                extra.getUsername(),
                extra.getServerName()
            );

        }
        setText(view, R.id.message, contentToDisplay);
    } */

    private void displayAvatar(final View view, final boolean isOwnMessageDAO) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
        if (imageView != null) {
            if (user == null) {
                imageView.setImageResource(R.drawable.default_gravatar);
            } else {
                Picasso.with(context)
                    .load(fetchGravatarUrl(isOwnMessageDAO))
                    .placeholder(R.drawable.default_gravatar)
                    .into(imageView);
            }
        }
    }

    private String fetchGravatarUrl(boolean ownMessageDAO) {
        if (ownMessageDAO) {
            return Session.getGravatarUrl();
        } else {
            return UrlFactory.buildGravatarUrl(user.getGravatarHash());
        }
    }

    private int fetchLayoutResource(final int position) {
        final int itemTypeNumber = getItemViewType(position);
        switch (itemTypeNumber) {
            case 0:
                return R.layout.list_item_message_right;
            case 1:
                return R.layout.list_item_message_left;
            default:
                throw new IllegalStateException("Unknown itemTypeNumber: " + itemTypeNumber);
        }
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
