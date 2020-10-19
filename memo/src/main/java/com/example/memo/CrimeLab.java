package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.memo.database.CrimeBaseHelper;
import com.example.memo.database.CrimeCursorWrapper;
import com.example.memo.database.CrimeDBSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    public static int mCurrentIndex = 0;
    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(context).getWritableDatabase();
        mCrimes = getCrimes();
    }

    // 增加记录
    public int addCrime(Crime crime) {
        mCrimes.add(crime);
        ContentValues values = getContentValues(crime);
        mSQLiteDatabase.insert(CrimeDBSchema.CrimeTable.NAME, null, values);
        return mCrimes.size();
    }

    // 更新记录
    public void updateCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        String uuid = crime.getId().toString();
        mSQLiteDatabase.update(CrimeDBSchema.CrimeTable.NAME, values, CrimeDBSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuid.toString()});
    }

    // 查询记录
    private CrimeCursorWrapper queryCrimes(String whereCause, String[] whereArgs) {
        Cursor cursor = mSQLiteDatabase.query(CrimeDBSchema.CrimeTable.NAME, null, whereCause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    // 删除记录
    public void deleteCrime(Crime crime) {
        mSQLiteDatabase.delete(CrimeDBSchema.CrimeTable.NAME, CrimeDBSchema.CrimeTable.Cols.UUID + " = ?", new String[]{crime.getId().toString()});
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    crimes.add(cursor.getCrime());
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
        getCrimes();
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        Log.d("File path", "getPhotoFile: " + filesDir);
        File folder = new File(filesDir + "/images/");
        if (!folder.exists()) {
            folder.mkdir();
        }
        return new File(folder, crime.getPhotoFilename());
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDBSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDBSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDBSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDBSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDBSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeDBSchema.CrimeTable.Cols.PHONE, crime.getPhone());

        return values;
    }
}
