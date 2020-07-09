package com.viktor.kh.dev.shoplist.controller.addProducts;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viktor.kh.dev.shoplist.model.Product;
import com.viktor.kh.dev.shoplist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleHolder>{

    private List<Product> productList = new ArrayList<>();
    private OnProductClickListener productClickListener;
    private OnProductLongClickListener productLongClickListener;

    public RecycleAdapter(OnProductClickListener productClickListener,OnProductLongClickListener productLongClickListener) {
        this.productClickListener = productClickListener;
        this.productLongClickListener = productLongClickListener;
    }

    public void setItems(ArrayList<Product> list){
        productList.addAll(list);
        notifyDataSetChanged();
    }

    // update the list
    public void update(ArrayList<Product> list){

        productList.clear();
        for (int i = 0; i < list.size(); i++) {
            productList.add(list.get(i));
        }
        notifyDataSetChanged();
    }

    public void readyProduct(Product product,int position,int newPosition){
        productList.remove(position);
        productList.add(newPosition,product);

        if(position==newPosition){
            notifyDataSetChanged();
        }else {
            notifyItemMoved(position,newPosition);
        }

    }


    public void addItem(Product product,int position){
        productList.add(product);
        Collections.sort(productList);
        notifyItemInserted(position);
    }

    public void delete_item(int position){
        productList.remove(position);
        notifyItemRemoved(position);
    }


    // create list item
    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_ready, viewGroup, false);

            return new RecycleHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item, viewGroup, false);
            return new RecycleHolder(view);
        }



    }

    @Override
    public void onBindViewHolder(RecycleHolder recycleHolder, int i) {
           recycleHolder.bind(productList.get(i));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



    class RecycleHolder extends RecyclerView.ViewHolder{
        private TextView text;
        public void bind(Product product){
            if (getItemViewType()==1){
                text.setPaintFlags(text.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                text.setText(product.getName());
            }else {
                text.setText(product.getName());
            }

        }


        // create appearance and handle clicks
        public RecycleHolder( View itemView) {

            super(itemView);
           text = itemView.findViewById(R.id.productName);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int position  = getLayoutPosition();
                   productClickListener.onProductClick(position);
               }
           });
           itemView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   int position = getLayoutPosition();

                   productLongClickListener.onProductLongClick(position);
                   return true;
               }
           });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(productList.get(position).isReady()){
            return 1;
        }else {
            return 0;
        }

    }

    public interface OnProductClickListener{
        void onProductClick(int position);
    }

    public interface  OnProductLongClickListener{
        void onProductLongClick(int position);
    }


}
