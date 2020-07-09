package com.viktor.kh.dev.shoplist.controller.recipe;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.viktor.kh.dev.shoplist.controller.SettingActivity;
import com.viktor.kh.dev.shoplist.model.Product;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.controller.MainActivity;
import com.viktor.kh.dev.shoplist.utils.OnBackPressedListener;
import com.viktor.kh.dev.shoplist.controller.recipes.RecipesFragment;
import com.viktor.kh.dev.shoplist.R;


public class RecipeFragment extends Fragment implements OnBackPressedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecipePageAdapter adapter;
    private String recipeName;
    private View view;
    private Repository repository;
    private static SharedPreferences myPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

          Bundle bundle = getArguments();
          recipeName =  bundle.getString("tableName", "");
           view = inflater.inflate(R.layout.recipe_tab_layout_fragment,container,false);
           tabLayout = view.findViewById(R.id.tabLayoutInRecipe);
           viewPager = view.findViewById(R.id.view_pager_in_recipe);
           adapter = new RecipePageAdapter(getChildFragmentManager());
           Fragment recipeProductsFragment = new RecipeProductsFragment();
           Fragment recipeTextFragment = new RecipeTextFragment();
           recipeProductsFragment.setArguments(bundle);
           recipeTextFragment.setArguments(bundle);
           adapter.addFragment(recipeProductsFragment,getActivity().getString(R.string.products));
           adapter.addFragment(recipeTextFragment,getActivity().getString(R.string.recipe));

           viewPager.setAdapter(adapter);
           tabLayout.setupWithViewPager(viewPager);
           initToolBar();
           repository = new Repository(getActivity());
           myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
           return view;
    }




    private void initToolBar(){
     ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
     if(actionBar!=null){
         actionBar.setTitle(Helper.convertNameTableToVisual(recipeName));
     }
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_in_recipe, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if(id == R.id.share_item){
            share();
        }

        return super.onOptionsItemSelected(item);
    }



    private void share(){
        boolean  sortByDate = myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
        StringBuilder sb = new StringBuilder();
        sb.append(Helper.convertNameTableToVisual(recipeName)+"\n"+"\n");
        sb.append(repository.getText(recipeName)+"\n"+"\n");
        sb.append(getString(R.string.products)+ ":"+"\n");
        for(Product product: repository.readDbFromRecipe(recipeName,sortByDate)){
            sb.append(product.getName()+"\n");
        }

        Helper.shareText(sb.toString(),getActivity());
    }



    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new RecipesFragment()).commit();

    }
}
