package com.example.homesync.Model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    // ATRIBUTOS
    private String code;
    private List<User> userList;
    private List<Task> taskList;
    private List<Product> shoppingList;



    // CONSTRUCTORES
    public Group() {
    }

    public Group(String code, List<User> userList, List<Task> taskList, List<Product> shoppingList) {
        this.code = code;
        this.userList = userList;
        this.taskList = taskList;
        this.shoppingList = shoppingList;
    }

    public Group(String code, List<User> userList) {
        this.code = code;
        this.userList = userList;
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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
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
    public void addUser(User user){
        userList.add(user);
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
