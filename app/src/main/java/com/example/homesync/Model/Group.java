package com.example.homesync.Model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homesync.Dialogs.DialogJoinGroup;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.MainActivity;
import com.example.homesync.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DatabaseError;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Group {

    // ATRIBUTOS
    private String code;
    private List<String> userIdList;
    private List<Task> taskList;
    private List<PurchaseProduct> shoppingList;



    // CONSTRUCTORES
    public Group() {
    }

    public Group(String code, List<String> userIdList, List<Task> taskList, List<PurchaseProduct> shoppingList) {
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

    public List<PurchaseProduct> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<PurchaseProduct> shoppingList) {
        this.shoppingList = shoppingList;
    }



    // METODOS

    /**
     * Muestra el dialog para crear un grupo.
     * @param idUser
     * @param context
     */
    public static void createGroup(String idUser, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_dialog_create_group, null);

        RadioButton radioButtonMonthly = dialogView.findViewById(R.id.radioButtonMonthly);
        RadioButton radioButtonWeekly = dialogView.findViewById(R.id.radioButtonWeekly);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activityA, R.style.CustomAlertDialog);
        builder.setView(dialogView);

        // Botón Confirmar
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (radioButtonMonthly.isChecked()) {
                    generateUniqueCodeAndCreateGroup(idUser, "monthly", context);
                } else if (radioButtonWeekly.isChecked()) {
                    generateUniqueCodeAndCreateGroup(idUser, "weekly", context);
                }
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.activityA, "Acción cancelada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create().show();
    }

    /**
     * Genera un id para el grupo y lo crea.
     * @param idUser
     * @param context
     */
    private static void generateUniqueCodeAndCreateGroup(String idUser, String lastReset, Context context) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        String finalCode = code.toString();

        FirebaseRealtimeDatabase.checkGroupExists(finalCode, context, new FirebaseRealtimeDatabase.OnGroupCheckListener() {
            @Override
            public void onGroupExists(boolean exists) {
                if (!exists) {
                    // El grupo no existe: se puede crear
                    FirebaseRealtimeDatabase.addUserToGroup(idUser, finalCode, true, lastReset, context);
                    Toast.makeText(context, "Grupo creado", Toast.LENGTH_SHORT).show();
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        Intent intent = activity.getIntent();
                        activity.finish();
                        activity.overridePendingTransition(0, 0); // Evita animación de salida
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0); // Evita animación de entrada
                    }
                } else {
                    // El grupo ya existe: volver a intentar con un nuevo código
                    generateUniqueCodeAndCreateGroup(idUser, lastReset, context); // Llamada recursiva
                }
            }
        });
    }

    /**
     * Muestra el dialog para acceder a un grupo.
     * @param context
     */
    public static void joinGroup(Context context) {
        if (context instanceof FragmentActivity) {
            DialogJoinGroup dialog = DialogJoinGroup.newInstance("Introduce el código", "Código del grupo", "Código incorrecto");
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "DialogJoinGroup");
        }
    }
}
