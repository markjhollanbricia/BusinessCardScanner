package com.example.lenovo.businesscardscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler2 extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Last.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tbllast";
    public static final String COL_1 = " regid";
    public static final String COL_2 = " Name";
    public static final String COL_3 = " Username";
    public static final String COL_4 = " Email";
    public static final String COL_5 = " PhoneNo";

    public DBHandler2(Context context) {


        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_2 + " TEXT," + COL_3 + " TEXT,"
                + COL_4 + " TEXT," + COL_5 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        return cursor;
    }
    public List<String> getAllValues()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> list = new ArrayList<String>();
        String query = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do {
                list.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean insertData (String Name, String Username, String Email, String PhoneNo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put(COL_2, Name);
        value.put(COL_3, Username);
        value.put(COL_4, Email);
        value.put(COL_5, PhoneNo);


        long result = db.insertOrThrow(TABLE_NAME, null, value);

        if (result!=-1){
            return true;
        }
        else {
            return false;
        }
    }
    public Cursor Search(String text)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT COL_2 FROM "+ TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        return cursor;
    }

}
