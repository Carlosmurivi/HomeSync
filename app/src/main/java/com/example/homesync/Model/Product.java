package com.example.homesync.Model;

public class Product {

    // ATRIBUTOS
    private int id;
    private String name;
    private String amount;
    private String category;
    private boolean predetermined;



    // CONSTRUCTORES
    public Product() {
    }

    public Product(int id, String name, String amount, String category, boolean predetermined) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.predetermined = predetermined;
    }



    // GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPredetermined() {
        return predetermined;
    }

    public void setPredetermined(boolean predetermined) {
        this.predetermined = predetermined;
    }



    // METODOS
    public void makeDefault(){
        this.predetermined = true;
    }

    public void makeNonDefault(){
        this.predetermined = false;
    }

    public void editProduct(String name, String amount, String category, boolean predetermined){
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.predetermined = predetermined;
    }
}
