package com.ninetwozero.battlechat.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.ninetwozero.battlechat.json.chat.PresenceType;

import se.emilsjolander.sprinkles.typeserializers.SqlType;
import se.emilsjolander.sprinkles.typeserializers.TypeSerializer;

public class PresenceTypeSerializer implements TypeSerializer<PresenceType> {
    @Override
    public PresenceType unpack(Cursor cursor, String columnName) {
        final int id = cursor.getInt(cursor.getColumnIndex(columnName));
        return PresenceType.from(id);
    }

    @Override
    public void pack(PresenceType presenceType, ContentValues contentValues, String columnName) {
        contentValues.put(columnName, presenceType.getId());
    }

    @Override
    public String toSql(PresenceType presenceType) {
        return String.valueOf(presenceType.getId());
    }

    @Override
    public SqlType getSqlType() {
        return SqlType.INTEGER;
    }
}
