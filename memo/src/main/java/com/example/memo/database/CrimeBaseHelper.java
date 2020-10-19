package com.example.memo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author smaxlyb
 * @date 2020/4/21 14:25
 * website: https://smaxlyb.cn
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int Version = 3;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeDBSchema.CrimeTable.NAME +
                "( _id integer primary key autoincrement," +
                CrimeDBSchema.CrimeTable.Cols.UUID + "," +
                CrimeDBSchema.CrimeTable.Cols.TITLE + "," +
                CrimeDBSchema.CrimeTable.Cols.DATE + "," +
                CrimeDBSchema.CrimeTable.Cols.SOLVED + "," +
                CrimeDBSchema.CrimeTable.Cols.SUSPECT + "," +
                CrimeDBSchema.CrimeTable.Cols.PHONE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CrimeDBSchema.CrimeTable.NAME);
        onCreate(db);
    }
}
