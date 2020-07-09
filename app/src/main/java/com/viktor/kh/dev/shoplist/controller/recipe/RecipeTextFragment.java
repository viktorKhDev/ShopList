package com.viktor.kh.dev.shoplist.controller.recipe;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.R;

public class RecipeTextFragment extends Fragment {

    View view;
    private EditText recipeText;
    private FloatingActionButton fabEdit;
    private FloatingActionButton fabConfirm;
    private boolean editable = false;
    private String recipeName;
    private Repository repository;
    private TextView textDescription;
    private Animation animation;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.recipe_text_fragment,container,false);
        recipeText = view.findViewById(R.id.recipe_text);
        Bundle bundle = getArguments();
        recipeName =  bundle.getString("tableName", "");
        textDescription = view.findViewById(R.id.text_description);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim);
        fabEdit = view.findViewById(R.id.fab_edit_description);
        fabConfirm = view.findViewById(R.id.fab_confirm_description);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditableText();
            }
        });
        fabConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              setNoEditableText();
            }
        });
        repository = new Repository(getActivity());
        recipeText.setText(repository.getText(recipeName));
        recipeText.setEnabled(false);
        if(recipeText.getText().length()==0){
            textDescription.setVisibility(View.VISIBLE);
        }
        return view;
    }

      @Override
    public void onPause() {
        super.onPause();
          repository.setText(recipeName,recipeText.getText().toString());


    }

    @Override
    public void onStop() {
        super.onStop();
        repository.setText(recipeName,recipeText.getText().toString());

    }


    ////////////////////////////////////////////////////////////



    // enable description editing if inactive and off if active
    @SuppressLint("RestrictedApi")
    private void setEditableText(){


        if(!editable){
            editable = true;
            recipeText.setEnabled(true);
            Helper.initFocusAndShowKeyboard(recipeText,getActivity());
            textDescription.setVisibility(View.GONE);
            fabConfirm.setVisibility(View.VISIBLE);
            fabConfirm.startAnimation(animation);
            fabEdit.setVisibility(View.GONE);
            repository.setText(recipeName,recipeText.getText().toString());


        }

         descriptionVisibility();

    }




    @SuppressLint("RestrictedApi")
    private void setNoEditableText(){
        if(editable){
            Helper.cancelKeyboard(recipeText,getActivity());
            repository.setText(recipeName,recipeText.getText().toString());
            recipeText.setEnabled(false);
            editable = false;
            fabConfirm.setVisibility(View.GONE);
            fabEdit.setVisibility(View.VISIBLE);
            fabEdit.startAnimation(animation);
            descriptionVisibility();
        }

    }



    private void descriptionVisibility(){
        if(recipeText.getText().length()==0&&!editable){
            textDescription.setVisibility(View.VISIBLE);
        }else {
            textDescription.setVisibility(View.GONE);
        }
    }

}
