package com.example.homesync.Model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.R;

public class User {

    // ATRIBUTOS
    private String id;
    private String name;
    private String nickname;
    private String mail;
    private String password;
    private int points;
    private int goldMedals;
    private int silverMedals;
    private int bronzeMedals;
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
        this.goldMedals = 0;
        this.silverMedals = 0;
        this.bronzeMedals = 0;
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

    public int getGoldMedals() {
        return goldMedals;
    }

    public void setGoldMedals(int goldMedals) {
        this.goldMedals = goldMedals;
    }

    public int getSilverMedals() {
        return silverMedals;
    }

    public void setSilverMedals(int silverMedals) {
        this.silverMedals = silverMedals;
    }

    public int getBronzeMedals() {
        return bronzeMedals;
    }

    public void setBronzeMedals(int bronzeMedals) {
        this.bronzeMedals = bronzeMedals;
    }





    // METODOS

    public static void makeUserAdministrator(String idUser, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle("Hacer administrador");
        builder.setMessage("¿Quieres hacer administrador a este usuario?");

        // Botón Confirmar
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseRealtimeDatabase.updateUserAdministrator(idUser, true, context);
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    Intent intent = activity.getIntent();
                    activity.finish();
                    activity.overridePendingTransition(0, 0); // Evita animación de salida
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0); // Evita animación de entrada
                }
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    public static void makeUserNotAdministrator(String idUser, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle("Quitar de administrador");
        builder.setMessage("¿Quieres quitar de administrador a este usuario?");

        // Botón Confirmar
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseRealtimeDatabase.updateUserAdministrator(idUser, false, context);
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    Intent intent = activity.getIntent();
                    activity.finish();
                    activity.overridePendingTransition(0, 0); // Evita animación de salida
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0); // Evita animación de entrada
                }
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    public static void removeUserFromGroup(String idUser, String groupCode, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        builder.setTitle("Expulsar del grupo");
        builder.setMessage("¿Quieres expulsar del grupo a este usuario?");

        // Botón Confirmar
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseRealtimeDatabase.removeUserFromGroup(idUser, groupCode, context);
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    Intent intent = activity.getIntent();
                    activity.finish();
                    activity.overridePendingTransition(0, 0); // Evita animación de salida
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0); // Evita animación de entrada
                }
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }
}
