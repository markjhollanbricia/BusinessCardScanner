package com.example.lenovo.businesscardscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "Dataholder.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tblholder";
    public static final String COL_1 = " contactid";
    public static final String COL_2 = " Name";
    public static final String COL_3 = " Phonenumber";
    public static final String COL_4 = " Email";
    public static final String COL_5 = " Position";
    public static final String COL_6 = " Company";
    public static final String COL_7 = " Country";

    public static final String COL_8 = " Status";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_2 + " TEXT," + COL_3 + " TEXT,"
                + COL_4 + " TEXT," + COL_5 + " TEXT,"
                + COL_6 + " TEXT," + COL_7 + " TEXT,"
                + COL_8 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void delete(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_1
                + "=" + id + "");
    }
    public boolean insertData ( String Name, String Phonenumber, String Email, String Position, String Company, String Country, String Status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put(COL_2, Name);
        value.put(COL_3, Phonenumber);
        value.put(COL_4, Email);
        value.put(COL_5, Position);
        value.put(COL_6, Company);
        value.put(COL_7, Country);
        value.put(COL_8, Status);


        long result = db.insertOrThrow(TABLE_NAME, null, value);

        if (result!=-1){
            return true;
        }
        else {
            return false;
        }
    }




    public boolean UpdateData(String ID,String Name, String Pnum, String  Email, String Pos, String Company , String Country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, ID);
        cv.put(COL_2, Name);
        cv.put(COL_3, Pnum);
        cv.put(COL_4, Email);
        cv.put(COL_5, Pos);
        cv.put(COL_6, Company);
        cv.put(COL_7, Country);
        db.update(TABLE_NAME, cv, "ID = ?", new String[]{ID});
        return true;
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
    public Cursor getIdData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ COL_1 +" FROM " + TABLE_NAME, null);

        return cursor;
    }
    public Cursor getEventAttendance(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT *  FROM " + TABLE_NAME +
                " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public List<String> getAllValues()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> lists = new ArrayList<String>();
        String query = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst())
        {
            do
            {
                lists.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lists;
    }
    public Cursor Search(String text)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME + " Where " + COL_2 + " Like '%"+text+"%'";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
    public void deletedata(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COL_1+ "=" +id,null);
        db.close();
    }

    public void update(String n,String pn,String e,String p,String c,String cn,int id)
    {
        SQLiteDatabase database =  getWritableDatabase();
        String sql = "Update tblHolder set Name=?, Phonenumber=?, Email=?, Position=?, Company=?, Country=? WHERE contactid=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1,n);
        statement.bindString(2,pn);
        statement.bindString(3,e);
        statement.bindString(4,p);
        statement.bindString(5,c);
        statement.bindString(6,cn);
        statement.bindDouble(7,(double)id);
        statement.executeInsert();
        database.close();
    }





}
