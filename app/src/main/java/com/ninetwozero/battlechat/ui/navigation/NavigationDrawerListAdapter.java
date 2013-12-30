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

package com.ninetwozero.battlechat.ui.navigation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseListAdapter;
import com.ninetwozero.battlechat.json.chat.PresenceType;
import com.ninetwozero.battlechat.json.chat.User;

import java.util.List;

public class NavigationDrawerListAdapter extends BaseListAdapter<User> {
    public NavigationDrawerListAdapter(final Context context, final List<User> users) {
        super(context, users);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public long getItemId(final int position) {
        return Long.parseLong(getItem(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final User user = getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_navigation_drawer, null);
        }

        setText(
            convertView,
            R.id.username,
            user.getUsername(),
            resolveColorForStatus(user.getPresenceType())
        );
        setText(
            convertView,
            R.id.online_status,
            resolveOnlineStatus(user.getPresenceType()),
            resolveColorForStatus(user.getPresenceType())
        );
        return convertView;
    }

    private int resolveColorForStatus(final PresenceType type) {

        switch (type) {
            case PLAYING_COOP:
            case PLAYING_MP:
            case PLAYING_ORIGIN:
                return R.color.presence_playing;
            case ONLINE_MOBILE:
            case ONLINE_WEB:
            case ONLINE_GAME:
            case ONLINE_TABLET:
            case ONLINE_ORIGIN:
                return R.color.presence_online;
            case AWAY_WEB:
            case AWAY_ORIGIN:
                return R.color.presence_away;
            default:
                return R.color.presence_offline;
        }
    }

    public static int resolveOnlineStatus(final PresenceType type) {
        switch (type) {
            case ONLINE_MOBILE:
            case ONLINE_WEB:
            case ONLINE_GAME:
            case ONLINE_TABLET:
            case ONLINE_ORIGIN:
                return R.string.label_online;
            case PLAYING_COOP:
            case PLAYING_MP:
            case PLAYING_ORIGIN:
                return R.string.label_playing;
            case AWAY_WEB:
            case AWAY_ORIGIN:
                return R.string.label_away;
            case OFFLINE:
                return R.string.label_offline;
            default:
                return R.string.label_unknown;
        }
    }

}
