package com.example.memo.database;

import android.database.Cursor;
import android.database.CursorWrapper;


import com.example.memo.Crime;

import java.sql.Date;
import java.util.UUID;

/**
 * @author smaxlyb
 * @date 2020/4/21 15:21
 * website: https://smaxlyb.cn
 */
public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.SUSPECT));
        String phone = getString(getColumnIndex(CrimeDBSchema.CrimeTable.Cols.PHONE));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setPhone(phone);
        return crime;
    }
}
