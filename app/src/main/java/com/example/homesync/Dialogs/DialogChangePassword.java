package com.example.homesync.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DialogChangePassword extends DialogFragment {

    private static final String ARG_MESSAGE1 = "mensaje1";
    private static final String ARG_MESSAGE2 = "mensaje2";
    private static final String ARG_ELEMENT1 = "elemento1";
    private static final String ARG_ELEMENT2 = "elemento2";
    private static final String MAIL_HOME_SYNC = "home.sync.information@gmail.com";

    private FirebaseAuth mAuth = SettingsFragment.getmAuth();
    private String code;

    // Método para crear una nueva instancia con parámetros
    public static DialogChangePassword newInstance(String message1, String element1, String message2, String element2) {
        DialogChangePassword fragment = new DialogChangePassword();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE1, message1);
        args.putString(ARG_MESSAGE2, message2);
        args.putString(ARG_ELEMENT1, element1);
        args.putString(ARG_ELEMENT2, element2);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Generar el codigo y mandar el correo con el codigo
        code = generateCode();
        sendMail(code);



        // Inflar el diseño personalizado
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_change_password, null);

        // Obtener referencias de los elementos de la vista
        TextView dialogMessage = view.findViewById(R.id.dialogMessage);
        TextInputEditText editText = view.findViewById(R.id.editText);
        TextInputLayout textLayout = view.findViewById(R.id.textLayout);
        //TextInputEditText passwordEditText = view.findViewById(R.id.passwordEditText);
        //TextInputLayout passwordTextLayout = view.findViewById(R.id.passwordLayout);
        Button buttonOk = view.findViewById(R.id.buttonOk);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        //Button passwordButtonOk = view.findViewById(R.id.passwordButtonOk);
        //Button passwordButtonCancel = view.findViewById(R.id.passwordButtonCancel);

        // Recuperar los valores pasados como argumentos
        String message1 = getArguments() != null ? getArguments().getString(ARG_MESSAGE1) : "Mensaje por defecto";
        String message2 = getArguments() != null ? getArguments().getString(ARG_MESSAGE2) : "Mensaje por defecto";
        String element1 = getArguments() != null ? getArguments().getString(ARG_ELEMENT1) : "";
        String element2 = getArguments() != null ? getArguments().getString(ARG_ELEMENT2) : "";

        // Asignar valores a los elementos de la UI
        dialogMessage.setText(message1);
        textLayout.setHint(element1); // Usamos "elemento" como hint (placeholder)


        // Configurar botón de cancelar
        buttonCancel.setOnClickListener(v -> dismiss());

        // Configurar botón de aceptar
        buttonOk.setOnClickListener(v -> {
            String codeEntered = editText.getText().toString();

            if (!codeEntered.trim().equals(code)) {
                textLayout.setError("El código es incorrecto");
            } else {
                dialogMessage.setText(message2);
                //passwordTextLayout.setHint(element2); // Usamos "elemento" como hint (placeholder)

                //passwordTextLayout.setVisibility(View.VISIBLE);
                //passwordEditText.setVisibility(View.VISIBLE);
                //passwordButtonOk.setVisibility(View.VISIBLE);
                //passwordButtonCancel.setVisibility(View.VISIBLE);

                editText.setVisibility(View.GONE);
                textLayout.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.GONE);
                buttonOk.setVisibility(View.GONE);

                /*FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        FirebaseRealtimeDatabase.updateUserNickname(user.getId(), nickname, MainActivity.activityA);

                        SettingsFragment.getNicknameUser().setText(nickname);

                        dismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Manejar error si es necesario
                    }
                });*/
            }
        });

        //passwordButtonCancel.setOnClickListener(v -> dismiss());

        /*passwordButtonOk.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();

            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().updatePassword(password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // La contraseña se actualizó correctamente
                                Toast.makeText(getContext(), "Se ha actualizado la contraseña", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                // Hubo un error en la actualización
                                Toast.makeText(getContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }
        });*/

        // Crear el diálogo
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        builder.setView(view);
        return builder.create();
    }

    private String generateCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Genera un número entre 100000 y 999999
        return String.valueOf(code);
    }

    private void sendMail(String code){
        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.host", "smtp.googlemail.com"); // Reemplaza con tu servidor SMTP
                props.put("mail.smtp.port", "465"); // Puerto SMTP

                try{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Session session = Session.getDefaultInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(MAIL_HOME_SYNC, "pmwp wgwo fvrf laem");
                        }
                    });

                    if(session != null){
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(MAIL_HOME_SYNC));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getMail()));
                        message.setContent("<html><body><h1>Codigo de Verificación</h1><p>Se esta intentando cambiar la contraseña de usuario " +
                                "en HomeSync. El código de verificación para cambiar la contraseña es:</p></br><h1>" + code + "</h1></body></html>", "text/html; charset=utf-8");
                        message.setSubject("Codigo de verificación");

                        // Enviar el correo
                        Transport.send(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}
