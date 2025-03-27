package com.example.homesync.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.MainActivity;
import com.example.homesync.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class DialogJoinGroup extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ELEMENT = "element";
    private static final String ARG_ERROR = "error";
    private static final String MAIL_HOME_SYNC = "home.sync.information@gmail.com";

    private FirebaseAuth mAuth = SettingsFragment.getmAuth();

    // Método para crear una nueva instancia con parámetros
    public static DialogJoinGroup newInstance(String message, String element, String error) {
        DialogJoinGroup fragment = new DialogJoinGroup();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_ELEMENT, element);
        args.putString(ARG_ERROR, error);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Inflar el diseño personalizado
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_change_password, null);

        // Obtener referencias de los elementos de la vista
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        TextInputEditText editText = view.findViewById(R.id.editText);
        TextInputLayout textLayout = view.findViewById(R.id.textLayout);
        Button buttonOk = view.findViewById(R.id.buttonOk);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        // Recuperar los valores pasados como argumentos
        String message = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "Mensaje por defecto";
        String element = getArguments() != null ? getArguments().getString(ARG_ELEMENT) : "";
        String error = getArguments() != null ? getArguments().getString(ARG_ERROR) : "";

        // Asignar valores a los elementos de la UI
        dialogMessage.setText(message);
        textLayout.setHint(element); // Usamos "elemento" como hint (placeholder)


        // Configurar botón de cancelar
        buttonCancel.setOnClickListener(v -> dismiss());

        // Configurar botón de aceptar
        buttonOk.setOnClickListener(v -> {
            String codeEntered = editText.getText().toString();

            joinGroup(mAuth.getUid(), codeEntered, textLayout, error);
        });

        // Crear el diálogo
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        builder.setView(view);
        return builder.create();
    }

    private void joinGroup(String userId, String code, TextInputLayout textLayout, String error){
        FirebaseRealtimeDatabase.checkGroupExists(code, MainActivity.activityA, new FirebaseRealtimeDatabase.OnGroupCheckListener(){
            @Override
            public void onGroupExists(boolean exists) {
                if(exists){
                    FirebaseRealtimeDatabase.addUserToGroup(userId, code, false, MainActivity.activityA);
                    MainActivity.activityA.recreate();
                    dismiss();
                } else {
                    textLayout.setError(error);
                }
            }
        });
    }
}
