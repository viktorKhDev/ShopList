package com.viktor.kh.dev.shoplist.controller.support;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viktor.kh.dev.shoplist.R;
import com.viktor.kh.dev.shoplist.controller.MainActivity;


public class SupportFragment extends Fragment  {

    View view;
    TextView info;
    TextView email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.support_fragment,container,false);
        info = view.findViewById(R.id.text_info);
        email = view.findViewById(R.id.text_email);
        info.setText(getString(R.string.support_text));
        email.setText(getString(R.string.support_email));
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        initToolBar();
        return view;
    }



    private void initToolBar(){
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(getString(R.string.reference));

        }

    }





}
