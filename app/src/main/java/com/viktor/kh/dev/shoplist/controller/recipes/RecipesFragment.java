package com.viktor.kh.dev.shoplist.controller.recipes;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.viktor.kh.dev.shoplist.utils.Constants;
import com.viktor.kh.dev.shoplist.utils.FollowText;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.controller.MainActivity;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.R;
import com.viktor.kh.dev.shoplist.controller.RecycleListsAdapter;
import com.viktor.kh.dev.shoplist.controller.recipe.RecipeFragment;

import java.util.ArrayList;

public class RecipesFragment extends Fragment implements FollowText.OnSearchTextChange {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddList;
    private ArrayList<String> recipeLists;
    private RecycleListsAdapter adapter;
    private Repository repository;
    private EditText autoTextView;
    private ImageButton closeSearch;
    private MenuItem menuItem;
    private View view;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recipes_fragment,container,false);
       repository = new Repository(getActivity());
        recipeLists = repository.readDbRecipes();
        initRecyclerView();
        autoTextView = getActivity().findViewById(R.id.auto_complete_text);
        autoTextView.setHint(getString(R.string.recipe_title));
        closeSearch = getActivity().findViewById(R.id.close_search);
        closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goneSearch();
            }
        });
        fabAddList = view.findViewById(R.id.fabAddList);
        fabAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addList();
            }
        });
        initToolBar();
        return view;
    }

    private void initToolBar(){
        String title = getActivity().getString(R.string.recipes);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(title.substring(0, 1).toUpperCase() + title.substring(1));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_in_lists, menu);
        menuItem = menu.findItem(R.id.search_item);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if(id == R.id.search_item){
            search();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(closeSearch.getVisibility()==View.VISIBLE){
            goneSearch();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(closeSearch.getVisibility()==View.VISIBLE){
            goneSearch();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    private void initAnim(){
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_start_anim);
        autoTextView.startAnimation(animation);
        closeSearch.startAnimation(animation);

    }


    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecycleListsAdapter.OnListClickListener listClickListener = new RecycleListsAdapter.OnListClickListener() {
            @Override
            public void onListClick(int position) {
                openList(recipeLists.get(position));
            }
        };
        RecycleListsAdapter.OnSetClickListener onSetClickListener = new RecycleListsAdapter.OnSetClickListener() {
            @Override
            public void onSetClick(int position) {
                renameList(position);
            }
        };

        RecycleListsAdapter.OnDelClicListener onDelClickListener = new RecycleListsAdapter.OnDelClicListener() {
            @Override
            public void onDelClick(int position) {
                deleteList(position);
            }
        };
        adapter = new RecycleListsAdapter(listClickListener,onSetClickListener,onDelClickListener,
                getActivity(), Constants.TYPE_RECIPES);
        adapter.setItems(recipeLists);
        recyclerView.setAdapter(adapter);
    }


    // create a list
    private void addList(){

        final Dialog dialogAddList = new Dialog(getActivity());
        dialogAddList.setContentView(R.layout.dialog_add);
        final EditText editName = dialogAddList.findViewById(R.id.text_del_product);
        editName.setHint(getString(R.string.recipe_title));
        Helper.initFocusAndShowKeyboard(editName,getActivity());
        Button buttonAddList = dialogAddList.findViewById(R.id.btn_yes);
        Button buttonCancel = dialogAddList.findViewById(R.id.btn_no);
        dialogAddList.setCancelable(true);
        dialogAddList.show();
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.cancelKeyboard(editName,getActivity());
                dialogAddList.dismiss();
            }
        });
        buttonAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = editName.getText().toString().trim();
                if (tableName.length()!=0||tableName==Helper.DOUBLE_QUOTES_REPLACE||tableName==Helper.QUOTES_REPLACE) {
                    String tableNameToFirstUpperCase = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
                    if(!checkContainsName(tableNameToFirstUpperCase)){
                        addToDB(tableNameToFirstUpperCase);
                        dialogAddList.dismiss();
                        scroll();
                    }else {
                        Helper.showToast(getString(R.string.such_a_recipe_exists),getActivity());
                    }

                }else {
                    Helper.showToast(getString(R.string.input_the_title),getActivity());
                }
            }
        });

    }



    private void addToDB(String tableName){
        recipeLists.add(tableName);
        adapter.update(recipeLists);
        repository.addRecipeToDB(tableName);
        scroll();
    }




    private void deleteList(final int position){
        final int itemPosition = position;
        final Dialog dialogDeleteList = new Dialog(getActivity());
        dialogDeleteList.setContentView(R.layout.dialog_delete_product);
        dialogDeleteList.setCancelable(true);
        TextView textView = dialogDeleteList.findViewById(R.id.text_del_product);
        textView.setText(getString(R.string.delete_recipe_q));
        Button ok = dialogDeleteList.findViewById(R.id.btn_del_product);
        Button no = dialogDeleteList.findViewById(R.id.btn_no_del_product);
        dialogDeleteList.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteList.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.deleteRecipe(recipeLists.get(itemPosition));
                recipeLists.remove(itemPosition);
                adapter.update(recipeLists);
                dialogDeleteList.dismiss();
                scroll();
            }
        });

    }

    // rename the list
    private void renameList(final int position){

        final EditText listName;
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add);
        listName = dialog.findViewById(R.id.text_del_product);
        listName.setFocusable(true);
        Button inputButton = dialog.findViewById(R.id.btn_yes);
        inputButton.setText(getString(R.string.yes));
        Button cancelButton = dialog.findViewById(R.id.btn_no);
        dialog.setCancelable(true);
        listName.setText(recipeLists.get(position));
        dialog.show();
        listName.requestFocus();
        final InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        listName.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    // handle pressing Enter
                    String s = listName.getText().toString().trim();
                    if(s.length() != 0||s==Helper.DOUBLE_QUOTES_REPLACE||s==Helper.QUOTES_REPLACE){
                        if(checkContainsName(s)){
                            // rename logic
                            recipeNameFromTo(recipeLists.get(position),s,position);
                        }
                    }else {
                        Helper.showToast(getString(R.string.input_the_title),getActivity());

                    }
                    return true;
                }
                return false;
            }
        });
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  s = listName.getText().toString();

                if(listName.getText().toString().trim().length() != 0){
                    if(!checkContainsName(s)){
                        // rename logic
                        dialog.dismiss();
                        recipeNameFromTo(recipeLists.get(position),s,position);
                    }

                }else {
                    Helper.showToast(getString(R.string.input_the_title),getActivity());

                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            }
        });
    }


    // open the list
    private void openList(String tableName){

     changeFragment(tableName);
    }

    private void search(){
        autoTextView.setVisibility(View.VISIBLE);
        closeSearch.setVisibility(View.VISIBLE);
        menuItem.setVisible(false);
        autoTextView.addTextChangedListener(new FollowText(this));
        initAnim();
    }

    private void goneSearch(){
        Helper.cancelKeyboard(autoTextView,getActivity());
        autoTextView.setText("");
        autoTextView.setVisibility(View.GONE);
        closeSearch.setVisibility(View.GONE);
        menuItem.setVisible(true);
        recipeLists = repository.readDbRecipes();
        adapter.update(recipeLists);
    }


    private void scroll(){
        // scroll to the last list
        recyclerView.scrollToPosition(recipeLists.size()-1);
    }




    private void recipeNameFromTo(String oldList,String newList,int position){
        String newListToFirstUpperCase = newList.substring(0, 1).toUpperCase() + newList.substring(1);
        repository.recipeNameFromTo(oldList,newListToFirstUpperCase);
        recipeLists.set(position,newListToFirstUpperCase);
        adapter.update(recipeLists);

    }

    private boolean checkContainsName(String name){

        boolean b = false;
        for(String st: recipeLists){
            String s = st.trim();
            if(s.equals(name.trim())) {
                b = true;
            }
        }
        return b;

    }

    private void changeFragment(String tableName) {
        Bundle bundle = new Bundle();
        bundle.putString("tableName", Helper.convertNameTableToDb(tableName));
        RecipeFragment fragment = new RecipeFragment();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
        if(closeSearch.getVisibility()==View.VISIBLE){
            goneSearch();
        }

    }

    @Override
    public void textChange(String s) {
        ArrayList<String> list = new ArrayList<>();
        for(String listName:repository.readDbRecipes()){
            if(listName.toLowerCase().contains(s.toLowerCase())){
                list.add(listName);
            }
        }
        recipeLists.clear();
        recipeLists.addAll(list);
        adapter.update(recipeLists);
    }
}
