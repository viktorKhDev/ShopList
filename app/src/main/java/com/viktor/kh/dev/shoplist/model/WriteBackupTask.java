package com.viktor.kh.dev.shoplist.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class WriteBackupTask extends AsyncTask<Uri,Void, Boolean> {


    private Context context;


    public WriteBackupTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        try {
            writeBackup(uris[0],context);

        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public void writeBackup(Uri uri, Context context) throws IOException{

        Repository repository = new Repository(context);

        ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri, "w");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(pfd.getFileDescriptor()));
            objectOutputStream.writeObject(repository.createBackupClass());
            objectOutputStream.close();
            pfd.close();

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
         if(aBoolean){
          Helper.showToast(context.getString(R.string.backup_file_created),context);
      }else {
          Helper.showToast(context.getString(R.string.error),context);
      }
    }
}
