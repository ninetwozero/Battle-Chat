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

public class SimpleAboutRow extends BaseAboutRow {
    private int subTitle;
    private String url;

    public SimpleAboutRow(final int title, final int subTitle, final String url) {
        super(title, Type.ITEM);
        this.subTitle = subTitle;
        this.url = url;
    }

    public int getSubTitle() {
        return subTitle;
    }

    public String getUrl() { return url; }
}
