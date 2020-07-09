package com.viktor.kh.dev.shoplist.model;

import java.io.Serializable;
import java.util.Date;

public class Product implements Comparable<Product>, Serializable {
    private String name;
    private Date date;
    private boolean ready = false;
    private boolean sortByDate = false;
    private static final long serialVersionUID = 1L;



    public Product(String name, boolean ready, Date date,boolean sortByDate) {
        this.name = name;
        this.date = date;
        this.ready = ready;
        this.sortByDate = sortByDate;
    }

    public Product(String name) {
        this.name = name;
        date = new Date();
        ready=false;
        sortByDate = true;

    }

    public Product(Product product){
        this.name = product.getName();
        this.date = product.getDate();
        this.ready = product.isReady();
        this.sortByDate = product.getSortByDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public Date getDate() {
        return date;
    }
    public void setReady(boolean ready) {
        ready = ready;
    }

    public boolean getSortByDate(){
        return this.sortByDate;
    }

   public void toScreenDb(){
        String s = getName();
        char[] c = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <c.length ; i++) {
            sb.append(c[i]);
            if(c[i]=='\''){
                sb.append('\'');
            }
        }
        setName(sb.toString());

    }

    @Override
    public int compareTo(Product o) {

     if(isReady()!=o.isReady()){
        if(isReady()==false&&o.isReady()==true){
            return -1;
        }
        return 1;
     }else {
         if(sortByDate){
            return getDate().compareTo(o.getDate());
         }
        return getName().compareTo(o.getName());
    }

    }

}
