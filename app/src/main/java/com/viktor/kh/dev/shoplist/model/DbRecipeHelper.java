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

public class DbRecipeHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "recipesDb";
    public static final String RECIPES_TABLE = "recipesTableaaeransdafdvct";
    public static final int DATABASE_VERSION = 1;
    public static final String DATE_RECIPE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_READY = "ready";
    public static final String KEY_TEXT = "recipetext";


    public DbRecipeHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public void renameTable(String oldTable,String newTable){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE "+ oldTable+ " RENAME TO " +newTable);
        db.close();
        updateName(oldTable,newTable);
    }
    public void createTable( String tableNameForCreate){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table "+ tableNameForCreate+ " ("+ KEY_ID + " integer primary key,"+ KEY_NAME+" text,"+KEY_DATE+" text,"+KEY_READY+" integer"+") ");
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, tableNameForCreate);
        values.put(KEY_DATE,new SimpleDateFormat(DATE_RECIPE_FORMAT).format(new Date()));
        db.insert(RECIPES_TABLE, null, values);
        db.close();
    }
    public void createRecipesTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table "+ RECIPES_TABLE+ " ("+ KEY_ID + " integer primary key,"+ KEY_NAME+" text,"+ KEY_DATE+" text,"+KEY_TEXT+" text"+") ");
        db.close();
    }

    public void deleteTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists "+ tableName);
        db.close();
        deleteRow(tableName,RECIPES_TABLE);
    }


    public void updateText(String recipeName,String text){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TEXT,text);
        db.update(RECIPES_TABLE,cv,KEY_NAME  +" = '" +recipeName+"'",null);
        db.close();
    }


    public String getText(String recipeName){
        Map<String,String> map = new HashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(RECIPES_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(KEY_NAME);
                int getTextIndex = cursor.getColumnIndex(KEY_TEXT);
                map.put(cursor.getString(getNameIndex)
                ,cursor.getString(getTextIndex));

            } while (cursor.moveToNext());
        }
        return map.get(recipeName);

    }


    public void updateName(String oldRecipeName,String newRecipeName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+RECIPES_TABLE+" SET "+KEY_NAME+" = '"+newRecipeName+"' WHERE "+ KEY_NAME+"  = '"+oldRecipeName+"'");
        db.close();
    }


    public void updateDate(String recipeName,Date date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+RECIPES_TABLE+" SET "+KEY_DATE+" = '"+new SimpleDateFormat(DATE_FORMAT).format(date)+"' WHERE "+ KEY_NAME+"  = '"+recipeName+"'");
        db.close();
    }

    public Date getRecipeDate(String listName){

        Map<String,String> map = new HashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(RECIPES_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(KEY_NAME);
                int getDateIndex = cursor.getColumnIndex(KEY_DATE);
                map.put(cursor.getString(getNameIndex)
                        ,cursor.getString(getDateIndex));

            } while (cursor.moveToNext());
        }
        try {
            return new SimpleDateFormat(DATE_RECIPE_FORMAT).parse(map.get(listName));
        } catch (ParseException e) {
            return null;
        }
    }
}
