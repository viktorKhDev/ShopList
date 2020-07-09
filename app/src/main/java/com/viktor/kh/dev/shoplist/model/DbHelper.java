package com.viktor.kh.dev.shoplist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "productsDb";
    private static final int DATABASE_VERSION = 1;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_LIST_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String KEY_ID = "id";
    public static final String LISTS_TABLE = "listsTableshwqtbbwq";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_READY = "ready";




    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void deleteRow(String name,String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName+ " WHERE "+KEY_NAME+"='"+name+"'");
        db.close();
    }

    public void updateReady(String name,int ready,String tableName){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+tableName+" SET "+KEY_READY+" = "+ready+" WHERE "+ KEY_NAME+"  = '"+name+"'");
        db.close();
    }
    public void createTable( String tableNameForCreate){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table "+ tableNameForCreate+ " ("+ KEY_ID + " integer primary key,"+ KEY_NAME+" text,"+KEY_DATE+" text,"+KEY_READY+" integer"+") ");
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tableNameForCreate);
        values.put(KEY_DATE,new SimpleDateFormat(DATE_LIST_FORMAT).format(new Date()));
        db.insert(LISTS_TABLE, null, values);
        db.close();
    }

    public void createListsTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table "+ LISTS_TABLE+ " ("+ KEY_ID + " integer primary key,"+ KEY_NAME+" text,"+KEY_DATE+" text"+") ");
        db.close();
    }

   public void deleteTable(SQLiteDatabase db,String tableName){
       db.execSQL("drop table if exists "+ tableName);
       db.close();
       deleteRow(tableName,LISTS_TABLE);
   }
   public void renameTable(String oldTable,String newTable){
       SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE "+ oldTable+ " RENAME TO " +newTable);
        db.close();
       updateName(oldTable,newTable);
   }

   public void clearAllTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM"+" "+tableName);
        db.close();
   }
    public void updateName(String oldRecipeName,String newRecipeName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+LISTS_TABLE+" SET "+KEY_NAME+" = '"+newRecipeName+"' WHERE "+ KEY_NAME+"  = '"+oldRecipeName+"'");
        db.close();
    }
    public void updateDate(String listName,Date date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+LISTS_TABLE+" SET "+KEY_DATE+" = '"+new SimpleDateFormat(DATE_FORMAT).format(date)+"' WHERE "+ KEY_NAME+"  = '"+listName+"'");
        db.close();
    }

    public Date getListDate(String listName){
        Map<String,String> map = new HashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(LISTS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(KEY_NAME);
                int getDateIndex = cursor.getColumnIndex(KEY_DATE);
                map.put(cursor.getString(getNameIndex)
                        ,cursor.getString(getDateIndex));

            } while (cursor.moveToNext());
        }
        try {
            return new SimpleDateFormat(DATE_LIST_FORMAT).parse(map.get(listName));
        } catch (ParseException e) {
            return null;
        }
    }
}
