package com.viktor.kh.dev.shoplist.controller.backup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.R;
import com.viktor.kh.dev.shoplist.controller.MainActivity;


public class BackupFragment extends Fragment {


    View view;
    Button btnSave;
    Button btnDownload;
    TextView infoText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.backup_fragment,container,false);
        infoText = view.findViewById(R.id.backup_text_warning);
        btnSave = view.findViewById(R.id.btn_save_file);
        btnDownload = view.findViewById(R.id.btn_download_file);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Helper.onBackupRead){
                    createFile("*/*","ShopListBackup");
                }

            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Helper.onBackupRead){
                    performFileSearch();
                }

            }
        });
        initToolBar();
        return view;
    }

    private void initToolBar(){
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(getString(R.string.backup));

        }

    }



    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        getActivity().startActivityForResult(intent, 1001);
    }



    private void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        getActivity().startActivityForResult(intent, 1000);
    }
}
