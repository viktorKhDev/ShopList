package com.viktor.kh.dev.shoplist.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProductList implements Serializable {

    private List<Product> products;
    private String name;
    private Date date;
    private static final long serialVersionUID = 1L;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
