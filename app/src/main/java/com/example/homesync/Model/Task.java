package com.example.homesync.Model;

public class Task {

    // ATRIBUTOS
    private int id;
    private String description;
    private int points;
    private String userId;
    private String imageUrl;
    private boolean predetermined;



    // CONSTRUCTORES
    public Task() {
    }

    public Task(int id, String description, int points, String userId, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.predetermined = predetermined;
    }

    public Task(int id, String description, int points, String userId, String imageUrl, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.predetermined = predetermined;
    }

    public Task(int id, String description, int points, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
        this.predetermined = predetermined;
    }

    public Task(String description, int points, String userId, boolean predetermined) {
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.predetermined = predetermined;
    }



    // GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPredetermined() {
        return predetermined;
    }

    public void setPredetermined(boolean predetermined) {
        this.predetermined = predetermined;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }





    // METODOS
    public void makeDefault(){
        this.predetermined = true;
    }

    public void makeNonDefault(){
        this.predetermined = false;
    }

    public void editTask(String description, int points, String userId, boolean predetermined){
        this.description = description;
        this.points = points;
        this.userId = userId;
        this.predetermined = predetermined;
    }
}
