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

package com.ninetwozero.battlechat.interfaces;

public class AboutInfoRow {
    private int title;
    private int subTitle;
    private String url;
    private Type type;

    public AboutInfoRow(final int title) {
        this.title = title;
        this.type = Type.HEADER;
    }

    public AboutInfoRow(final int title, final int subTitle, final String url) {
        this.title = title;
        this.subTitle = subTitle;
        this.url = url;
        this.type = Type.ITEM;
    }

    public int getTitle() {
        return title;
    }

    public int getSubTitle() {
        return subTitle;
    }

    public String getUrl() { return url; }

    public Type getType() {
        return type;
    }

    public static enum Type {
        HEADER,
        ITEM
    }
}
