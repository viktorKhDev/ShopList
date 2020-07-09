package com.viktor.kh.dev.shoplist.controller.addProducts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.viktor.kh.dev.shoplist.utils.ItemTouchHelperAdapter;
import com.viktor.kh.dev.shoplist.utils.ItemTouchHelperCallback;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.controller.MainActivity;
import com.viktor.kh.dev.shoplist.utils.OnBackPressedListener;
import com.viktor.kh.dev.shoplist.model.Product;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.R;
import com.viktor.kh.dev.shoplist.controller.SettingActivity;
import com.viktor.kh.dev.shoplist.controller.lists.ListsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;


public class AddFragment extends Fragment implements OnBackPressedListener, ItemTouchHelperAdapter {


    private boolean sortByDate = false;
    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
    private ArrayList<Product> products;
    private FloatingActionButton addP;
    private ImageButton btnYes;
    private ImageButton btnNo;
    private String tableName;
    private EditText textInputProduct;
    private Repository repository;
    private RelativeLayout addGroup;
    Animation animation;
    private static SharedPreferences myPref;
    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;
    View view;
    boolean sortNow;


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_add, container, false);
        repository = new Repository(getActivity());
        myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortByDate = myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
        sortNow = sortByDate;
        Bundle bundle = getArguments();
        tableName = bundle.getString("tableName", "");
        products = readFromDb();
        addGroup = view.findViewById(R.id.relative_add_product);
        initRecyclerView();
        initToolBar();
        initButtons();
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


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

    public void initButtons(){
        textInputProduct = view.findViewById(R.id.text_product);
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


    private void initToolBar(){
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(Helper.convertNameTableToVisual(tableName));

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_in_list,menu);
        super.onCreateOptionsMenu(menu, inflater);


        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        if ((!(clipboard.hasPrimaryClip()))
        ||(!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)))) {
            MenuItem item = menu.findItem(R.id.paste);
            item.setVisible(false);
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.clean) {
            // logic to clear the list
            if (products.size() > 0) {
                deleteAllProducts();
            }
        }
        if(id == R.id.add_recipe){
            addProductsFromRecipe();
        }

        if(id == R.id.share_item){
            share();
        }
        if(id == R.id.paste){
           paste();
        }

        return super.onOptionsItemSelected(item);
    }


    ///////////////////////////////////////////////////
    private void initRecyclerView() {

            recyclerView = view.findViewById(R.id.listProducts);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecycleAdapter.OnProductClickListener productClickListener = new RecycleAdapter.OnProductClickListener() {
                @Override
                public void onProductClick(int position) {
                    productReady(position);
                }
            };
            RecycleAdapter.OnProductLongClickListener productLongClickListener = new RecycleAdapter.OnProductLongClickListener() {
                @Override
                public void onProductLongClick(int position) {
                    renameItem(position);
                }
            };
            adapter = new RecycleAdapter(productClickListener, productLongClickListener);
            adapter.setItems(products);
            recyclerView.setAdapter(adapter);
            callback = new ItemTouchHelperCallback(this);
            touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);


    }

    private void requestRecycler(){
        Collections.sort(products);
        adapter.update(products);
        Helper.scroll(recyclerView, products);
    }



    public void initAnim(){
         animation = AnimationUtils.loadAnimation(getActivity(), R.anim.to_start_anim);
         addGroup.startAnimation(animation);
    }

    // add the product to the list
    public void addProduct() {

        addP.hide();
        addGroup.setVisibility(View.VISIBLE);
        initAnim();
        setMarginBottomForRecyclerView(true);
        Helper.scroll(recyclerView, products);
        Helper.initFocusAndShowKeyboard(textInputProduct,getActivity());
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = textInputProduct.getText().toString().trim();
                if (s.trim().length() != 0) {
                    if (!checkContainsName(s)) {
                        Date date = new Date();
                        Product product = new Product(s, false, date, sortByDate);
                       addProductToDb(product);
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
                textInputProduct.setText("");
                addGroup.setVisibility(View.GONE);
                addP.show();
                Helper.cancelKeyboard(textInputProduct,getActivity());
                setMarginBottomForRecyclerView(false);
                Helper.scroll(recyclerView, products);
            }
        });

    }


    private void addProductToDb(Product product){
        products.add(product);
        Collections.sort(products);
        Product product1 = new Product(product);
        repository.addProductToDb(product1,tableName);
        textInputProduct.setText("");
        int position = 0;
        for (int i = 0; i <products.size() ; i++) {
            if(product.compareTo(products.get(i))==0){
                position = i;
            }
        }
        adapter.addItem(product,position);
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

    // remove the product from the list
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

    private void share(){
        StringBuilder stringBuilder = new StringBuilder();
        for(Product product: products){
            stringBuilder.append(product.getName());
            stringBuilder.append("\n");
        }
       Helper.shareText(stringBuilder.toString(),getActivity());
    }


    private void paste(){
        try{
            String text = Helper.getClipboard(getActivity());

            String[] strings = text.split("\n");
            ArrayList<String> list = new ArrayList<>();
            for(Product product: products){
                list.add(product.getName());
            }
            for(String s: strings){
                if(!list.contains(s)){
                    Product product = new Product(s,false,new Date(),sortByDate);
                    products.add(product);

                    repository.addProductToDb(product,tableName);
                }
            }
          requestRecycler();
        }catch (Exception e){

        }
    }

    // clear product list
    private void deleteAllProducts() {
        final Dialog dialogDlel = new Dialog(getActivity());
        dialogDlel.setContentView(R.layout.dialog_delete_product);
        dialogDlel.setCancelable(true);
        dialogDlel.show();
        Button deleteButton = dialogDlel.findViewById(R.id.btn_del_product);
        Button noDeleteButton = dialogDlel.findViewById(R.id.btn_no_del_product);
        TextView textView = dialogDlel.findViewById(R.id.text_del_product);
        textView.setText(getString(R.string.clear_list));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.clearAllListTable(tableName);
                products.clear();
                adapter.update(products);
                dialogDlel.dismiss();

            }
        });

        noDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogDlel.dismiss();
            }
        });

    }

    // remove the product from the database
    private void deleteProductFromDB(int position) {

        Product product = new Product(products.get(position));
        product.toScreenDb();
        repository.deleteProductFromTable(product.getName(),tableName);
        products.remove(position);
        adapter.delete_item(position);
    }


    private ArrayList<Product> readFromDb() {
      return repository.readProductsFromTable(tableName,sortByDate);
    }

    // get the list of products from the selected recipe
    private void addProductsFromRecipe() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.recipes_for_dialog));
        String[] strings = repository.getRecipesNames();
        Arrays.sort(strings);
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                products.addAll(repository.addProductsFromRecipe(sortByDate,tableName,repository.getRecipesNames()[position]));
                Collections.sort(products);
                adapter.update(products);
                Helper.scroll(recyclerView, products);
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // mark the product as purchased and vice versa
    private void productReady(int position) {

        boolean ready = products.get(position).isReady();
        String name = products.get(position).getName();
        Date date = products.get(position).getDate();
        products.remove(position);
        int numReady;

        if ((ready == false)) {
            ready = true;
            numReady = 1;
        } else {
            ready = false;

            numReady = 0;
        }
        Product product = new Product(name, ready, date, sortByDate);
        products.add(product);
        Product product1 = new Product(product);
                product1.toScreenDb();
                repository.changeProductReady(product1.getName(),numReady,tableName);
        Collections.sort(products);
        int newPosition = 0;
        for (int i = 0; i < products.size();i++){
          if(product.compareTo(products.get(i))==0){
              newPosition=i;
          }
        }
        adapter.readyProduct(product,position,newPosition);
        if(position==0){
            recyclerView.scrollToPosition(0);
        }

    }

    private void renameItem(final int position){
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
                Helper.cancelKeyboard(editText,getActivity());
            }
        });

        dialog.show();
        Helper.initFocusAndShowKeyboard(editText,getActivity());
    }

    ///////////////////////////////////////////////////////


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
    public void onBackPressed() {

        if(addGroup.getVisibility()==View.VISIBLE){
            textInputProduct.setText("");
            addGroup.setVisibility(View.GONE);
            addP.show();
            Helper.cancelKeyboard(textInputProduct,getActivity());
            setMarginBottomForRecyclerView(false);
            Helper.scroll(recyclerView, products);
        }else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new ListsFragment()).commit();
        }

    }


    @Override
    public void onItemDismiss(int position) {
        deleteProduct(position);
    }
}

