package com.example.homesync.Model;

public class Task {

    // ATRIBUTOS
    private int id;
    private String description;
    private int points;
    private boolean predetermined;



    // CONSTRUCTORES
    public Task() {
    }

    public Task(int id, String description, int points, boolean predetermined) {
        this.id = id;
        this.description = description;
        this.points = points;
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

    public void editTask(String description, int points, boolean predetermined){
        this.description = description;
        this.points = points;
        this.predetermined = predetermined;
    }
}
