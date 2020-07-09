package com.viktor.kh.dev.shoplist.utils;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.viktor.kh.dev.shoplist.model.Product;
import java.util.ArrayList;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;



public class Helper {

    public static final String QUOTES_REPLACE = "Y68BtEzs3r3AY45g";
    public static final String DOUBLE_QUOTES_REPLACE = "V3nEC4R6k61NF3ib";

    public static boolean onBackupRead = false;



    // scrolling to the last not finished product
    public static void scroll(RecyclerView recyclerView, ArrayList<Product> products) {

        int countFinished = 0;
        for (Product product : products) {
            if (product.isReady()) countFinished++;
        }
        recyclerView.scrollToPosition(products.size() - countFinished - 1);
    }

    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }


    // convert the name of the table in the database into a visual view
    public static String convertNameTableToVisual(String tableName){

        String visualName = tableName.substring(1,tableName.length()-1);
        String s = visualName.replaceAll(DOUBLE_QUOTES_REPLACE,"\"").replaceAll(QUOTES_REPLACE,"\'");
        return s;

    }


    // convert the string to create the table name in the database
    public static String convertNameTableToDb(String name){

        StringBuilder sb = new StringBuilder();

        String s = name.replaceAll("\"",DOUBLE_QUOTES_REPLACE).replaceAll("\'",QUOTES_REPLACE);

        sb.append('"');
        sb.append(s);
        sb.append('"');

        return  sb.toString();

    }

    public static String getClipboard(Context context){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);

        String pasteData = "";

       // If it does contain data, decide if you can handle the data.
        if (!(clipboard.hasPrimaryClip())) {



        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text

        } else {

            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            pasteData = item.getText().toString();

        }
        return pasteData;
    }


    public static void initFocusAndShowKeyboard(final EditText et, final Context context) {
        et.requestFocus();
        et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 0);

    }

    public static void cancelKeyboard(View view, Context context){
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    public static void shareText(String text,Context context){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }




}
