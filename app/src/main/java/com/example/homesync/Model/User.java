package com.example.homesync.Model;

public class User {

    // ATRIBUTOS
    private String id;
    private String name;
    private String nickname;
    private String mail;
    private String password;
    private int points;
    private int weeklyPoints;
    private int monthlyPoints;
    private String groupCode;
    private String image;
    private boolean administrator;



    // CONSTRUCTOR
    public User() {
    }

    public User(String id, String name, String nickname, String mail, String password, int points, int weeklyPoints, int monthlyPoints, String groupCode, String image) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.mail = mail;
        this.password = password;
        this.points = points;
        this.weeklyPoints = weeklyPoints;
        this.monthlyPoints = monthlyPoints;
        this.groupCode = groupCode;
        this.image = image;
        this.administrator = false;
    }

    public User(String id, String name, String nickname, String mail) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.mail = mail;
        this.password = "";
        this.points = 0;
        this.weeklyPoints = 0;
        this.monthlyPoints = 0;
        this.groupCode = "";
        this.image = "https://res.cloudinary.com/dlclglmr6/image/upload/v1739489498/usuario_xftkhf.png";
        this.administrator = false;
    }

    public User(String id, String name, String nickname, String mail, String password) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.mail = mail;
        this.password = password;
        this.points = 0;
        this.weeklyPoints = 0;
        this.monthlyPoints = 0;
        this.groupCode = "";
        this.image = "https://res.cloudinary.com/dlclglmr6/image/upload/v1739489498/usuario_xftkhf.png";
        this.administrator = false;
    }



    // GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWeeklyPoints() {
        return weeklyPoints;
    }

    public void setWeeklyPoints(int weeklyPoints) {
        this.weeklyPoints = weeklyPoints;
    }

    public int getMonthlyPoints() {
        return monthlyPoints;
    }

    public void setMonthlyPoints(int monthlyPoints) {
        this.monthlyPoints = monthlyPoints;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }



    // METODOS
    public void removeFromGroup(){
        this.points = 0;
        this.weeklyPoints = 0;
        this.monthlyPoints = 0;
        this.groupCode = "";
        this.administrator = false;
    }
}
