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

package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.json.chat.User;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    public int compare(final User u1, final User u2) {
        final int leftPriority = u1.getPresenceType().getListPriority();
        final int rightPriority = u2.getPresenceType().getListPriority();

        if (leftPriority > rightPriority) {
            return -1;
        } else if (leftPriority < rightPriority) {
            return 1;
        }
        return u1.getUsername().compareToIgnoreCase(u2.getUsername());
    }
}
