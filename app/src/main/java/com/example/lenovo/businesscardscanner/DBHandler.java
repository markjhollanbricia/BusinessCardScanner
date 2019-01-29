package com.example.lenovo.businesscardscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Picture;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "Dataholder.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tblholder";
    public static final String COL_1 = " contactid";
    public static final String COL_2 = " Firstname";
    public static final String COL_3 = " Lastname";
    public static final String COL_4 = " Phonenumber";
    public static final String COL_5 = " Email";
    public static final String COL_6 = " Position";
    public static final String COL_7 = " Company";
    public static final String COL_8 = " Country";
    public static final String COL_9 = " Picture";
    public static final String COL_10 = " Status";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_2 + " TEXT," + COL_3 + " TEXT,"
                + COL_4 + " TEXT," + COL_5 + " TEXT,"
                + COL_6 + " TEXT," + COL_7 + " TEXT,"
                + COL_8 + " TEXT," + COL_9 + " blob,"
                + COL_10 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData ( String Firstname, String Lastname, String Phonenumber, String Email, String Position, String Company, String Country, String Status, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put(COL_2, Firstname);
        value.put(COL_3, Lastname);
        value.put(COL_4, Phonenumber);
        value.put(COL_5, Email);
        value.put(COL_6, Position);
        value.put(COL_7, Company);
        value.put(COL_8, Country);
        value.put(COL_9, image);
        value.put(COL_10, Status);


        long result = db.insertOrThrow(TABLE_NAME, null, value);

        if (result!=-1){
            return true;
        }
        else {
            return false;
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG.PNG, 0, outputStream);
        return outputStream.toByteArray();
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
            do
            {
                list.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public Cursor Search(String text)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME + " Where " + COL_2 + " Like '%"+text+"%'";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }





}
