package com.example.homesync.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Fragments.SettingsFragment;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class DialogEditNickname extends DialogFragment {

    private static final String ARG_MESSAGE = "mensaje";
    private static final String ARG_ELEMENT = "elemento";

    private FirebaseAuth mAuth = SettingsFragment.getmAuth();

    // Método para crear una nueva instancia con parámetros
    public static DialogEditNickname newInstance(String mensaje, String elemento) {
        DialogEditNickname fragment = new DialogEditNickname();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, mensaje);
        args.putString(ARG_ELEMENT, elemento);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflar el diseño personalizado
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_edit_1, null);

        // Obtener referencias de los elementos de la vista
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        TextInputEditText editText = view.findViewById(R.id.editText);
        TextInputLayout textLayout = view.findViewById(R.id.textLayout);
        Button buttonOk = view.findViewById(R.id.buttonOk);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        // Recuperar los valores pasados como argumentos
        String mensaje = getArguments() != null ? getArguments().getString(ARG_MESSAGE) : "Mensaje por defecto";
        String elemento = getArguments() != null ? getArguments().getString(ARG_ELEMENT) : "";

        // Asignar valores a los elementos de la UI
        dialogMessage.setText(mensaje);
        textLayout.setHint(elemento); // Usamos "elemento" como hint (placeholder)

        // Configurar botón de cancelar
        buttonCancel.setOnClickListener(v -> dismiss());

        // Configurar botón de aceptar
        buttonOk.setOnClickListener(v -> {
            String nickname = editText.getText().toString();

            if (nickname.trim().isEmpty()) {
                textLayout.setError("Este campo es obligatorio");
            } else {
                FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        FirebaseRealtimeDatabase.updateUserNickname(user.getId(), nickname, MainActivity.activityA);

                        /*Intent intent = requireActivity().getIntent();
                        requireActivity().finish();
                        startActivity(intent);*/

                        SettingsFragment.getNicknameUser().setText(nickname);

                        dismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Manejar error si es necesario
                    }
                });
            }
        });

        // Crear el diálogo
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        builder.setView(view);
        return builder.create();
    }
}
