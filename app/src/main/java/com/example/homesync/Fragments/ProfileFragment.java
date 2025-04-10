package com.example.homesync.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.homesync.Dialogs.DialogJoinGroup;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.example.homesync.Prueba;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();


    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();

        ImageView image = view.findViewById(R.id.imageView);
        TextView titulo = view.findViewById(R.id.textView);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        Button joinGroupButton = view.findViewById(R.id.joinGroupButton);
        FloatingActionButton fab = view.findViewById(R.id.fab);


        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    joinGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);
                } else {
                    titulo.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    Glide.with(ProfileFragment.this).load(user.getImage()).into(image);
                    titulo.setText(user.getNickname());
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al generar los elementos");
                Toast.makeText(getContext(), "Error al generar los elementos", Toast.LENGTH_SHORT).show();
            }
        });


        createGroupButton.setOnClickListener(v -> createGroup());

        joinGroupButton.setOnClickListener(v -> joinGroup());






        // Configuración del click listener del FAB
        fab.setOnClickListener(v -> {
            // Crear el diálogo
            Dialog dialog = new Dialog(MainActivity.activityA);
            dialog.setContentView(R.layout.activity_dialog_create_task);
            dialog.setCancelable(true);

            // Referencias a los botones
            TextInputLayout nameTaskLayout = dialog.findViewById(R.id.nameTaskLayout);
            TextInputEditText nameTaskEditText = dialog.findViewById(R.id.nameTaskEditText);
            TextInputLayout pointsLayout = dialog.findViewById(R.id.pointsLayout);
            TextInputEditText pointsEditText = dialog.findViewById(R.id.pointsEditText);
            Switch predeterminedSwitch = dialog.findViewById(R.id.predeterminedSwitch);
            ImageView btnInsertImage = dialog.findViewById(R.id.imageTask);
            Button btnCreateTask = dialog.findViewById(R.id.createButton);
            Button btnCancel = dialog.findViewById(R.id.backButton);

            // Acción para añadir la fotografia
            btnInsertImage.setOnClickListener(v1 -> {
                // Se abre la camara y se hace una foto.
                Toast.makeText(getContext(), "Abriendo cámara...", Toast.LENGTH_SHORT).show();
            });

            // Acción para crear la tarea
            btnCreateTask.setOnClickListener(v1 -> {
                // Se recogen todos los datos, se comprueba que todo este completo, y se crea una nueva tarea
                Toast.makeText(getContext(), "Creando tarea...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            // Acción para cancelar
            btnCancel.setOnClickListener(v1 -> dialog.dismiss());

            // Mostrar el diálogo
            dialog.show();







            /*Task task = new Task(1, "Tarea 1", 100, mAuth.getUid(), true);
            FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback(){
                @Override
                public void onSuccess(User user) {
                    FirebaseRealtimeDatabase.addTaskToGroup(task, user.getGroupCode(), MainActivity.activityA);
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Erro durante el proceso", Toast.LENGTH_SHORT).show();
                }
            });*/
        });


        return view;
    }

    private void createGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activityA, R.style.CustomAlertDialog);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Quieres crear un grupo?");

        // Botón Confirmar
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final boolean[] flag = {false};
                StringBuilder code = new StringBuilder(6);

                do {
                    for (int i = 0; i < 6; i++) {
                        int index = RANDOM.nextInt(CHARACTERS.length());
                        code.append(CHARACTERS.charAt(index));
                    }
                    FirebaseRealtimeDatabase.checkGroupExists(code.toString(), MainActivity.activityA, new FirebaseRealtimeDatabase.OnGroupCheckListener(){
                        @Override
                        public void onGroupExists(boolean exists) {
                            if(!exists){
                                flag[0] = true;
                            }
                        }
                    });
                } while (flag[0]);

                FirebaseRealtimeDatabase.addUserToGroup(mAuth.getUid(), code.toString(), true, MainActivity.activityA);

                Toast.makeText(getContext(), "Grupo creado", Toast.LENGTH_SHORT).show();
                MainActivity.activityA.recreate();
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

    private void joinGroup(){
        DialogJoinGroup dialog = DialogJoinGroup.newInstance("Introduce el código", "Código del grupo", "Código incorrecto");
        dialog.show(getActivity().getSupportFragmentManager(), "DialogJoinGroup");
    }
}