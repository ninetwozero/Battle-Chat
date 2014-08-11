package com.ninetwozero.battlechat.dao.migration;

import android.database.sqlite.SQLiteDatabase;

import com.ninetwozero.battlechat.dao.MessageDAO;
import com.ninetwozero.battlechat.dao.UserDAO;

import se.emilsjolander.sprinkles.Migration;

public class InitialMigration extends Migration {
    @Override
    protected void doMigration(SQLiteDatabase db) {
        createMessageTable(db);
        createUserTable(db);
    }

    private void createMessageTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + MessageDAO.TABLE_NAME + "(" +
                "rowId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "author TEXT," +
                "content TEXT," +
                "timestamp INTEGER," +
                "userId TEXT" +
                ")"
        );
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + UserDAO.TABLE_NAME + "(" +
                "rowId INTEGER PRIMARY KEY," +
                "username TEXT," +
                "gravatarhash TEXT," +
                "presence TEXT," +
                "localUserId TEXT" +
                ")"
        );
    }
}
