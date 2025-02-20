package com.example.homesync.Model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    // ATRIBUTOS
    private String code;
    private List<String> userIdList;
    private List<Task> taskList;
    private List<Product> shoppingList;



    // CONSTRUCTORES
    public Group() {
    }

    public Group(String code, List<String> userIdList, List<Task> taskList, List<Product> shoppingList) {
        this.code = code;
        this.userIdList = userIdList;
        this.taskList = taskList;
        this.shoppingList = shoppingList;
    }

    public Group(String code, List<String> userIdList) {
        this.code = code;
        this.userIdList = userIdList;
        this.taskList = new ArrayList<>();
        this.shoppingList = new ArrayList<>();
    }



    // GETTES AND SETTERS
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Product> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<Product> shoppingList) {
        this.shoppingList = shoppingList;
    }



    // METODOS
    public void addUser(String id){
        userIdList.add(id);
    }

    public void addTask(Task task){
        taskList.add(task);
    }

    public void removeTask(Task task){
        taskList.remove(task);
    }

    public void addProduct(Product product){
        shoppingList.add(product);
    }

    public void removeProduct(Product product){
        shoppingList.remove(product);
    }

    public void emptyShoppingList(){
        for(Product product: shoppingList){
            if(!product.isPredetermined()){
                shoppingList.remove(product);
            }
        }
    }
}
