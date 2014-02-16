/*
 *
 * 	This file is part of BattleChat
 *
 * 	BattleChat is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 * 	BattleChat is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * /
 */

package com.ninetwozero.battlechat.datatypes;

import com.ninetwozero.battlechat.base.BaseAboutRow;

public class HeaderAboutRow extends BaseAboutRow {
    private int childCount;

    public HeaderAboutRow(final int title, final int childCount) {
        super(title, Type.HEADER);
        this.childCount = childCount;
    }

    public int getChildCount() {
        return childCount;
    }
}
