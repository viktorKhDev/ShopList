package com.viktor.kh.dev.shoplist.controller;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viktor.kh.dev.shoplist.model.Product;
import com.viktor.kh.dev.shoplist.model.Repository;
import com.viktor.kh.dev.shoplist.utils.Constants;
import com.viktor.kh.dev.shoplist.utils.Helper;
import com.viktor.kh.dev.shoplist.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecycleListsAdapter extends RecyclerView.Adapter<RecycleListsAdapter.RecycleListsHolder> {

    private List<String> lists = new ArrayList<>();
    private OnListClickListener onListClickListener;
    private OnDelClicListener onDelClicListener;
    private OnSetClickListener onSetClickListener;
    private Context context;
    private Repository repository;
    private final DateFormat  sdf = new SimpleDateFormat("dd.MM.yyyy");;
    private String adapterType;
    private static SharedPreferences myPref;
    private boolean sortByDate = false;



    public RecycleListsAdapter(OnListClickListener onListClickListener,
                               OnSetClickListener onSetClickListener,
                               OnDelClicListener onDelClickListener,
                               Context context,
                               String adapterType){
        this.onDelClicListener = onDelClickListener;
        this.onListClickListener = onListClickListener;
        this.onSetClickListener = onSetClickListener;
        this.context = context;
        repository = new Repository(context);
        this.adapterType = adapterType;
        myPref = PreferenceManager.getDefaultSharedPreferences(context);
        sortByDate = myPref.getBoolean(SettingActivity.MainSettingFragment.APP_PREFERENCES_SORT,false);
    }

    // update the list
    public void update(ArrayList<String> list){

        lists.clear();
        for (int i = 0; i < list.size(); i++) {
            lists.add(list.get(i));
        }
        notifyDataSetChanged();
    }
    public void setItems(ArrayList<String> list){
        lists.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecycleListsHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);
        return new RecycleListsHolder(view);
    }

    @Override
    public void onBindViewHolder( RecycleListsHolder recicleListsHolder, int i) {
         recicleListsHolder.bind(lists.get(i),adapterType);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class RecycleListsHolder extends RecyclerView.ViewHolder{
        private TextView text;
        private TextView textDate;
        private TextView textListReady;
        private AppCompatImageView imageSet;
        private AppCompatImageView imageDel;


        public void bind(String listName,String typeAdapter){
            text.setText(listName);
            if(typeAdapter == Constants.TYPE_LISTS){
                ArrayList<Product>  list = repository.readProductsFromTable(Helper.convertNameTableToDb(listName),sortByDate);
                int containsReady = 0;
                for(Product product:list){
                    if(product.isReady()){
                        containsReady++;
                    }
                }

                textListReady.setText(containsReady+"/"+list.size());

                textDate.setText(sdf.format(repository.getListDate(Helper.convertNameTableToDb(listName))));

            }else {
                textListReady.setVisibility(View.GONE);

                textDate.setText(sdf.format(repository.getRecipeDate(Helper.convertNameTableToDb(listName))));

            }

        }



        // create appearance and handle clicks
        public RecycleListsHolder(View itemView) {

            super(itemView);
            text = itemView.findViewById(R.id.listName);
            imageSet = itemView.findViewById(R.id.edit_image);
            imageDel = itemView.findViewById(R.id.delete_image);
            textDate = itemView.findViewById(R.id.text_list_date);
            textListReady = itemView.findViewById(R.id.text_list_ready);
            imageDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  int position = getLayoutPosition();
                  onDelClicListener.onDelClick(position);
                }
            });

            imageSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    onSetClickListener.onSetClick(position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position  = getLayoutPosition();
                    onListClickListener.onListClick(position);
                }
            });

        }
    }





    public interface OnDelClicListener{
        void onDelClick(int position);
    }

    public interface OnListClickListener{
        void onListClick(int position);
    }

    public interface OnSetClickListener{
        void onSetClick(int position);
    }
}
