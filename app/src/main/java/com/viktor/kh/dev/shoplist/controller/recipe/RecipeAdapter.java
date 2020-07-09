package com.viktor.kh.dev.shoplist.controller.recipe;

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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private List<Product> productList = new ArrayList<>();
    private RecipeAdapter.OnProductLongClickListener productLongClickListener;

    public RecipeAdapter( RecipeAdapter.OnProductLongClickListener productLongClickListener) {
        
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
    public RecipeAdapter.RecipeHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item, viewGroup, false);

          return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder( RecipeHolder recipeHolder, int i) {
        recipeHolder.bind(productList.get(i));
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    class RecipeHolder extends RecyclerView.ViewHolder{
        private TextView text;


        // create appearance and handle clicks
        public RecipeHolder(View itemView) {

            super(itemView);
            text = itemView.findViewById(R.id.productName);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getLayoutPosition();
                    productLongClickListener.onProductLongClick(position);
                    return true;
                }
            });
        }

        public void bind(Product product){
            text.setText(product.getName());
        }


    }



    public interface OnProductLongClickListener{
        void onProductLongClick(int position);
    }
}
