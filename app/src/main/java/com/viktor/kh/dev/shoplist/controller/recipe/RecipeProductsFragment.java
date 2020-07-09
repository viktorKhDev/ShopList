package com.viktor.kh.dev.shoplist.controller.recipe;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.viktor.kh.dev.shoplist.utils.ItemTouchHelperAdapter;
import com.viktor.kh.dev.shoplist.utils.ItemTouchHelperCallback;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.model.Product;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.R;
import com.viktor.kh.dev.shoplist.controller.SettingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class RecipeProductsFragment extends Fragment implements ItemTouchHelperAdapter {
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private ArrayList<Product> products;
    private FloatingActionButton addP;
    private String tableName;
    Repository repository;
    private boolean sortByDate;
    private RelativeLayout addGroup;
    private ImageButton btnYes;
    private ImageButton btnNo;
    private EditText productName;
    private static SharedPreferences myPref;
    Animation addGroupAnim;
    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;
    View view;
    boolean sortNow;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.recipe_products_fragment,container,false);
        repository = new Repository(getActivity());
        myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortByDate = myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
        sortNow = sortByDate;
        Bundle bundle = getArguments();
        tableName =  bundle.getString("tableName", "");
        products = repository.readDbFromRecipe(tableName, sortByDate);
        addGroup = view.findViewById(R.id.relative_add_product);
        initRecyclerView();
        addP = view.findViewById(R.id.addProduct);
        addP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
        initButtons();
        return view;
    }


    ////////////////////////////////////////////////////////////


    @Override
    public void onResume() {
        super.onResume();
        sortByDate = myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
        if(sortNow!=sortByDate){
            sortNow = sortByDate;
            ArrayList<Product> list = new ArrayList<>();
            list.addAll(products);
            products.clear();
            for(Product product:list){
                products.add(new Product(product.getName(),
                        product.isReady(),
                        product.getDate(),
                        sortByDate));
            }
            requestRecycler();
        }


    }

   private void initAnim(){
        addGroupAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.to_start_anim);
        addGroup.startAnimation(addGroupAnim);
    }


    private void initButtons(){
        productName = view.findViewById(R.id.text_product);
        addP = view.findViewById(R.id.addProduct);
        btnYes = view.findViewById(R.id.btn_accept_product);
        btnNo = view.findViewById(R.id.btn_no_product);
        addP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();

            }
        });

    }


    //////////////////////////////////////////////////////

    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.listProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecipeAdapter.OnProductLongClickListener productLongClickListener = new RecipeAdapter.OnProductLongClickListener() {
            @Override
            public void onProductLongClick(int position) {
                renameProduct(position);
            }
        };
        adapter = new RecipeAdapter(productLongClickListener);
        adapter.setItems(products);
        recyclerView.setAdapter(adapter);
        callback = new ItemTouchHelperCallback(this);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void deleteProduct(final int position) {
        final Dialog dialogDel = new Dialog(getActivity());
        dialogDel.setContentView(R.layout.dialog_delete_product);
        dialogDel.setCancelable(true);
        dialogDel.show();
        Button deleteButton = dialogDel.findViewById(R.id.btn_del_product);
        Button noDeleteButton = dialogDel.findViewById(R.id.btn_no_del_product);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductFromDB(position);
                dialogDel.dismiss();

            }
        });


        noDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.update(products);
                dialogDel.dismiss();
            }
        });

    }

    // remove the product from the database
    private void deleteProductFromDB(int position) {

        Product product = products.get(position);
        product.toScreenDb();
        repository.deleteProductFromRecipe(product.getName(), tableName);
        products.remove(position);
        adapter.delete_item(position);

    }


    // add the product to the list
    private void addProduct() {

        addP.hide();
        addGroup.setVisibility(View.VISIBLE);
        initAnim();
        setMarginBottomForRecyclerView(true);
        Helper.scroll(recyclerView, products);
        Helper.initFocusAndShowKeyboard(productName,getActivity());
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = productName.getText().toString().trim();
                if (s.length() != 0) {
                    if(!checkContainsName(s)){
                        Date date = new Date();
                        Product product = new Product(productName.getText().toString().trim(), false, date,sortByDate);
                       addProductToDb(product);
                       productName.setText("");
                        Helper.scroll(recyclerView, products);
                    }else {
                        Helper.showToast(getString(R.string.such_a_product_exists), getActivity());
                    }

                } else {
                    Helper.showToast(getString(R.string.input_the_title), getActivity());

                }

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productName.setText("");
                addGroup.setVisibility(View.GONE);
                addP.show();
                Helper.cancelKeyboard(productName,getActivity());
                setMarginBottomForRecyclerView(false);
                Helper.scroll(recyclerView, products);
            }
        });

    }

    private void addProductToDb(Product product){
        products.add(product);
        Collections.sort(products);
        Product product1 = new Product(product);

        repository.addProductToDbRecipe(product1,tableName);
        int position = 0;
        for (int i = 0; i <products.size() ; i++) {
            if(product.compareTo(products.get(i))==0){
                position = i;
            }
        }
        adapter.addItem(product,position);
    }

    private void renameProduct(final int position){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_rename_item);
        dialog.setCancelable(true);
        Button dialogYes = dialog.findViewById(R.id.btn_yes);
        Button dialogNo = dialog.findViewById(R.id.btn_no);
        final EditText editText = dialog.findViewById(R.id.text_product_set_name);
        editText.setText(products.get(position).getName());
        dialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString().trim();
                if(s.length()>0){
                    if(!checkContainsName(s)){
                        Product product = products.get(position);
                        deleteProductFromDB(position);
                        product.setName(s);
                        addProductToDb(product);
                        dialog.dismiss();
                        adapter.update(products);
                    }else {
                        Helper.showToast(getString(R.string.such_a_product_exists),getActivity());
                    }

                }else {
                    Helper.showToast(getString(R.string.enter_the_title),getActivity());
                }

            }
        });

        dialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Helper.initFocusAndShowKeyboard(editText,getActivity());

    }

    private void setMarginBottomForRecyclerView(boolean isMargin){
        RelativeLayout relativeLayout = view.findViewById(R.id.recycler_layout);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
        if(isMargin){
            float dp = getActivity().getResources().getDisplayMetrics().density;
            int margin = 60;
            int totalMargin =(int)  dp * margin;
            relativeParams.setMargins(0, 0, 0, totalMargin);
            relativeLayout.setLayoutParams(relativeParams);
        }else {
            relativeParams.setMargins(0, 0, 0, 0);
            relativeLayout.setLayoutParams(relativeParams);
        }

    }
    private void requestRecycler(){
        Collections.sort(products);
        adapter.update(products);
        Helper.scroll(recyclerView, products);
    }


    private boolean checkContainsName(String name) {

        boolean b = false;
        for (Product product : products) {
            String s = product.getName().trim();
            if (s.equals(name.trim())) {
                b = true;
            }
        }
        return b;

    }

    @Override
    public void onItemDismiss(int position) {
        deleteProduct(position);
    }

}
