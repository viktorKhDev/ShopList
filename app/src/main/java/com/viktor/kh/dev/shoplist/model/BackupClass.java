package com.viktor.kh.dev.shoplist.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BackupClass implements Serializable {
    private ArrayList<ProductList> productLists;
    private ArrayList<Recipe> recipes;
    private static final long serialVersionUID = 1L;


    public ArrayList<ProductList> getProductLists() {
        return productLists;
    }

    public void setProductLists(ArrayList<ProductList> productLists) {
        this.productLists = productLists;
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

}
