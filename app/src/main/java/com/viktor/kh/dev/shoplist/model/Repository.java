package com.viktor.kh.dev.shoplist.model;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.viktor.kh.dev.shoplist.utils.BackupListener;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.controller.SettingActivity;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Repository {



 private Context context;
 private DbHelper dbHelper;
 private DbRecipeHelper dbRecipeHelper;
 private static SharedPreferences myPref;
 private BackupListener backupListener;



    public Repository(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        dbRecipeHelper = new DbRecipeHelper(context);
        myPref = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public Repository(Context context, BackupListener backupListener){
     this(context);
     this.backupListener = backupListener;
    }

    public void firstRun(){
        dbRecipeHelper.createRecipesTable();
        dbHelper.createListsTable();
    }


    public void writeBackup(Uri uri){
      WriteBackupTask writeBackupTask = new WriteBackupTask(context);
      writeBackupTask.execute(uri);
     }

    public void readBackup(Uri uri){
        ReadBackupTask readBackupTask = new ReadBackupTask(context,backupListener);
        readBackupTask.execute(uri);

    }

    public BackupClass createBackupClass(){

     BackupClass backupClass = new BackupClass();
     backupClass.setProductLists(getAllLists());
     backupClass.setRecipes(getAllRecipes());
     return backupClass;

    }

    public void useBackupClass(BackupClass backupClass){
        setAllProductLists(backupClass.getProductLists());
        setAllRecipes(backupClass.getRecipes());
    }



    // get a list of tables with products
    public ArrayList<String> readDbLists(){

     ArrayList<String> tblNames = new ArrayList<>();
        if(dbHelper!=null){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor c = db.query(dbHelper.LISTS_TABLE, null, null, null, null, null, null);

            if (c.moveToFirst()) {
                do {
                    int getNameIndex = c.getColumnIndex(DbHelper.KEY_NAME);
                    tblNames.add(Helper.convertNameTableToVisual(c.getString(getNameIndex)));
                } while (c.moveToNext());
            }

        }
        dbHelper.close();
        return tblNames;
    }


    // add a list
    public void addList(String tableName){

        dbHelper.createTable(Helper.convertNameTableToDb(tableName));
 }


    // delete the list
  public void deleteList(String listName){

      SQLiteDatabase db = dbHelper.getWritableDatabase();
      dbHelper.deleteTable(db, Helper.convertNameTableToDb(listName));
      dbHelper.close();
  }

    // rename the recipe
    public void productsFromTo(String oldList,String newList){

        dbHelper.renameTable(Helper.convertNameTableToDb(oldList), Helper.convertNameTableToDb(newList));
  }


    // add the product to the database
    public void addProductToDb(Product product,String tableName) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        DateFormat format = new SimpleDateFormat(DbHelper.DATE_FORMAT);
        values.put(DbHelper.KEY_NAME, product.getName());
        values.put(DbHelper.KEY_DATE, format.format(product.getDate()));

        if (product.isReady() == false) {
            values.put(DbHelper.KEY_READY, 0);
        } else {
            values.put(DbHelper.KEY_READY, 1);
        }

        db.insert(tableName, null, values);
        dbHelper.close();

    }


    // get the list of products from the selected recipe
    public ArrayList<Product> addProductsFromRecipe(final boolean sortByDate, final String tableName,String recipeName) {

        final ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbRecipeHelper.getWritableDatabase();

                Cursor cursor = db.query(Helper.convertNameTableToDb(recipeName), null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    do {
                        int getNameIndex = cursor.getColumnIndex(DbHelper.KEY_NAME);
                        int getReadyIndex = cursor.getColumnIndex(DbHelper.KEY_READY);
                        boolean ready;
                        if (cursor.getInt(getReadyIndex) == 0) {
                            ready = false;
                        } else {
                            ready = true;
                        }
                        Date date = new Date();
                        Product product = new Product(cursor.getString(getNameIndex), ready, date, sortByDate);
                        product.setName(product.getName() + " (" + recipeName.toLowerCase() + ")");
                        addProductToDb(product,tableName);
                        products.add(product);

                    } while (cursor.moveToNext());

                }
                dbHelper.close();




        return products;
    }






    // populate the list from the database
    public ArrayList<Product> readProductsFromTable(String tableName,boolean sortByDate) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        ArrayList<Product> list = new ArrayList<>();
          if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(DbHelper.KEY_NAME);
                int getReadyIndex = cursor.getColumnIndex(DbHelper.KEY_READY);
                int getDateIndex = cursor.getColumnIndex(DbHelper.KEY_DATE);
                DateFormat format = new SimpleDateFormat(DbHelper.DATE_FORMAT);
                boolean ready;
                if (cursor.getInt(getReadyIndex) == 0) {
                    ready = false;
                } else {
                    ready = true;
                }
                Date date = null;
                try {
                    date = format.parse(cursor.getString(getDateIndex));
                } catch (ParseException e) {

                } finally {
                    dbHelper.close();
                }
                Product product = new Product(cursor.getString(getNameIndex), ready, date, sortByDate);
                list.add(product);
            } while (cursor.moveToNext());
        }


        dbHelper.close();
        Collections.sort(list);

        if(list.size()>0){
            return list;
        }
        return new ArrayList<>();

    }

    public void deleteProductFromTable(String name,String tableName){
        dbHelper.deleteRow(name, tableName);
        dbHelper.close();
    }


    // clear the list
    public void clearAllListTable(String tableName){

     dbHelper.clearAllTable(tableName);
        dbHelper.close();
    }




    // change readiness
    public void changeProductReady(String name,int numReady,String tableName){

        dbHelper.updateReady(name,numReady,tableName);
        dbHelper.close();
    }



    public String [] getRecipesNames(){
        // получение списка с именами рецептов
        ArrayList<String> tblNames = new ArrayList();
        tblNames.addAll(readDbRecipes());
        final String[] recipes = new String[tblNames.size()];
        tblNames.toArray(recipes);
        return recipes;
    }

    public void deleteProductFromRecipe(String productName,String tableName){
        dbRecipeHelper.deleteRow(productName,tableName);
      dbRecipeHelper.close();
    }


    // add the product to the database
    public void addProductToDbRecipe(Product product,String tableName) {

        SQLiteDatabase db = dbRecipeHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        DateFormat format = new SimpleDateFormat(DbRecipeHelper.DATE_FORMAT);
        values.put(DbRecipeHelper.KEY_NAME,product.getName());
        values.put(DbRecipeHelper.KEY_DATE,format.format(product.getDate()));
        if(product.isReady()==false){
            values.put(DbRecipeHelper.KEY_READY,0);
        }else {
            values.put(DbRecipeHelper.KEY_READY,1);
        }

        db.insert(tableName,null,values);
        dbRecipeHelper.close();
    }



    // populate the list from the database
    public ArrayList<Product> readDbFromRecipe(String tableName,boolean sortByDate) {

        SQLiteDatabase db = dbRecipeHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        ArrayList<Product> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(DbHelper.KEY_NAME);
                int getReadyIndex = cursor.getColumnIndex(DbHelper.KEY_READY);
                int getDateIndex = cursor.getColumnIndex(DbHelper.KEY_DATE);
                DateFormat format = new SimpleDateFormat(DbHelper.DATE_FORMAT);
                boolean ready;
                if (cursor.getInt(getReadyIndex) == 0) {
                    ready = false;
                } else {
                    ready = true;
                }
                Date date = null;
                try {
                    date = format.parse(cursor.getString(getDateIndex));
                } catch (ParseException e) {

                } finally {
                    dbRecipeHelper.close();
                }
                Product product = new Product(cursor.getString(getNameIndex), ready, date,sortByDate);
                list.add(product);
            } while (cursor.moveToNext());
        }


        dbRecipeHelper.close();
        Collections.sort(list);
        return list;
    }


    public String getText(String recipe){
       return dbRecipeHelper.getText(recipe);
    }

    public void setText(String recipe,String text){
        dbRecipeHelper.updateText(recipe,text);
    }




    // get a list of recipes
    public ArrayList<String> readDbRecipes() {

        ArrayList<String> tblNames = new ArrayList();
        SQLiteDatabase db = dbRecipeHelper.getWritableDatabase();
        Cursor cursor = db.query(dbRecipeHelper.RECIPES_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int getNameIndex = cursor.getColumnIndex(DbRecipeHelper.KEY_NAME);
                tblNames.add(Helper.convertNameTableToVisual(cursor.getString(getNameIndex)));
            } while (cursor.moveToNext());
        }
        dbRecipeHelper.close();
        return tblNames;
    }


    // add the recipe to the database
    public void addRecipeToDB(String tableName){

        dbRecipeHelper.createTable(Helper.convertNameTableToDb(tableName));
        dbRecipeHelper.close();
  }
    // delete the recipe
    public void deleteRecipe(String recipeName){

        dbRecipeHelper.deleteTable(Helper.convertNameTableToDb(recipeName));
        dbRecipeHelper.close();
  }

    // rename the recipe
    public void recipeNameFromTo(String oldList,String newList){

        String newListToFirstUpperCase = newList.substring(0, 1).toUpperCase() + newList.substring(1);
        dbRecipeHelper.renameTable(Helper.convertNameTableToDb(oldList), Helper.convertNameTableToDb(newListToFirstUpperCase));

    }






    // get a list of all recipes
    public ArrayList<Recipe> getAllRecipes(){

        ArrayList<Recipe> list = new ArrayList<>();
        for(String recipeName:readDbRecipes()){
            boolean sort =  myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
            Recipe recipe = new Recipe();
            recipe.setName(recipeName);
            recipe.setProducts(readDbFromRecipe(Helper.convertNameTableToDb(recipeName),sort));
            recipe.setText(getText(Helper.convertNameTableToDb(recipeName)));
            recipe.setDate(getRecipeDate(Helper.convertNameTableToDb(recipeName)));
            list.add(recipe);
        }

        return list;
    }


    // get a list of all lists
    public  ArrayList<ProductList> getAllLists(){

        ArrayList<ProductList> lists = new ArrayList<>();
        for(String listName:readDbLists()){
            boolean sort =  myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
            ProductList productList = new ProductList();
            productList.setName(listName);
            productList.setDate(getListDate(Helper.convertNameTableToDb(listName)));
            productList.setProducts(readProductsFromTable(Helper.convertNameTableToDb(listName),sort));
            lists.add(productList);
        }
        return lists;
    }

    // overwrite all recipes
    public void setAllRecipes(ArrayList<Recipe> recipes){

        for(Recipe recipe: getAllRecipes()){
            deleteRecipe(recipe.getName());
        }
        for (Recipe recipe:recipes){
            addRecipeToDB(recipe.getName());
            for (Product product: recipe.getProducts()){
                addProductToDbRecipe(product, Helper.convertNameTableToDb(recipe.getName()));
            }
            setText(Helper.convertNameTableToDb(recipe.getName()),recipe.getText());
            dbRecipeHelper.updateDate(Helper.convertNameTableToDb(recipe.getName()),recipe.getDate());
        }
    }

    // overwrite all lists
    public void setAllProductLists(ArrayList<ProductList> productLists){

        for(ProductList productList: getAllLists()){
            deleteList(productList.getName());
        }
        for(ProductList productList:productLists){
            addList(productList.getName());
            for(Product product:productList.getProducts()){
                addProductToDb(product, Helper.convertNameTableToDb(productList.getName()));
            }
            dbHelper.updateDate(Helper.convertNameTableToDb(productList.getName()),productList.getDate());
        }
    }



    public Date getListDate(String listName){
        return dbHelper.getListDate(listName);
    }

    public Date getRecipeDate(String recipeName){
        return dbRecipeHelper.getRecipeDate(recipeName);
    }

}
