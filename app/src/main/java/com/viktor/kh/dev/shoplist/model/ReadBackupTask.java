package com.viktor.kh.dev.shoplist.model;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.viktor.kh.dev.shoplist.utils.BackupListener;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


public class ReadBackupTask extends AsyncTask<Uri,Void,Boolean> {

    Context context;
    BackupListener backupListener;

    public ReadBackupTask(Context context, BackupListener backupListener) {
        this.context = context;
        this.backupListener = backupListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        backupListener.onPreBackup();
        Helper.onBackupRead = true;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {

        try {
            readBackup(uris[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void readBackup(Uri uri) throws IOException, ClassNotFoundException {

        Repository repository = new Repository(context);
        InputStream inputStream = null;

            inputStream = context.getContentResolver().openInputStream(uri);

            ObjectInputStream objectInputStream = null;

            objectInputStream = new ObjectInputStream(inputStream);

            BackupClass backupClass = (BackupClass)objectInputStream.readObject();
            repository.useBackupClass(backupClass);

            objectInputStream.close();


    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Helper.onBackupRead = false;
          if(!aBoolean){
              backupListener.onError();
            Helper.showToast(context.getString(R.string.backup_read_error),context);
          }else {
             backupListener.onBackupRead();
          }
    }
}
