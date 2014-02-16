package com.ninetwozero.battlechat.base;

public class BaseAboutRow {
    protected final int title;
    protected final BaseAboutRow.Type type;

    public BaseAboutRow(final int title, final BaseAboutRow.Type type) {
        this.title = title;
        this.type = type;
    }

    public int getTitle() {
        return title;
    }

    public BaseAboutRow.Type getType() {
        return type;
    }

    public static enum Type {
        HEADER,
        ITEM
    }
}
