package com.viktor.kh.dev.shoplist.utils;
import android.text.Editable;
import android.text.TextWatcher;



public class FollowText implements TextWatcher {

    private OnSearchTextChange onSearchTextChange;

    public FollowText(OnSearchTextChange onSearchTextChange) {
        this.onSearchTextChange = onSearchTextChange;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        onSearchTextChange.textChange(s.toString());
    }

    public interface OnSearchTextChange{
        void textChange(String s);
    }
}
