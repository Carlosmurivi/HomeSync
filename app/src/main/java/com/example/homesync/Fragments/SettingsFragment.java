package com.example.homesync.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homesync.CloudinaryDataBase;
import com.example.homesync.Dialogs.DialogChangePassword;
import com.example.homesync.Dialogs.DialogEditNickname;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
import com.example.homesync.Login;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.User;
import com.example.homesync.Prueba;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SettingsFragment extends Fragment {

    private static final String DEFAULT_PROFILE_PICTURE = "https://res.cloudinary.com/dlclglmr6/image/upload/v1739489498/usuario_xftkhf.png";
    private static final int PICK_IMAGE_REQUEST = 1;
    private String urlFoto;
    private Uri selectedImageUri;
    private boolean imagenAñadida = false;
    private static FirebaseAuth mAuth;
    private ImageView imageProfile;
    private static TextView nicknameUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings_fragment, container, false);


        mAuth = FirebaseAuth.getInstance();

        imageProfile = view.findViewById(R.id.imageProfile);
        nicknameUser = view.findViewById(R.id.nicknameUser);
        TextView mailUser = view.findViewById(R.id.mailUser);
        ImageButton changeNickname = view.findViewById(R.id.changeNickname);
        Button changePassword = view.findViewById(R.id.changePassword);
        Button leaveGroup = view.findViewById(R.id.leaveGroup);
        Button logOut = view.findViewById(R.id.logOut);



        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(user.getGroupCode().trim().equals("")) {
                    imageProfile.setVisibility(View.VISIBLE);
                    nicknameUser.setVisibility(View.VISIBLE);
                    mailUser.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.VISIBLE);
                    changeNickname.setVisibility(View.VISIBLE);
                    logOut.setVisibility(View.VISIBLE);
                } else {
                    imageProfile.setVisibility(View.VISIBLE);
                    nicknameUser.setVisibility(View.VISIBLE);
                    mailUser.setVisibility(View.VISIBLE);
                    changeNickname.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.VISIBLE);
                    leaveGroup.setVisibility(View.VISIBLE);
                    logOut.setVisibility(View.VISIBLE);
                }
                Glide.with(SettingsFragment.this)
                        .load(user.getImage())
                        .skipMemoryCache(true) // Evita la caché en RAM
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Evita la caché en disco
                        .into(imageProfile);
                Glide.with(SettingsFragment.this).load(user.getImage()).into(imageProfile);
                nicknameUser.setText(user.getNickname());
                mailUser.setText("@" + user.getMail());
            }
            @Override
            public void onFailure(Exception e) {

            }
        });




        // Cambiar Imagen de Perfil
        imageProfile.setOnClickListener(v -> openGallery());


        // Cambiar Apodo
        changeNickname.setOnClickListener(v -> {
            DialogEditNickname dialog = DialogEditNickname.newInstance("Introduce tu apodo", "Nuevo apodo");
            dialog.show(getActivity().getSupportFragmentManager(), "DialogEditNickname");
        });


        // Cambiar Contraseña
        changePassword.setOnClickListener(v -> dialogChangePassword());


        // Abandonar grupo
        leaveGroup.setOnClickListener(v -> leaveGroup());


        // Cerrar Sesión
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.activityA.finish();
                startActivity(new Intent(MainActivity.activityA, Index.class));
            }
        });

        return view;
    }

    private void openGallery() {
        // Crear el diálogo
        Dialog dialog = new Dialog(MainActivity.activityA);
        dialog.setContentView(R.layout.activity_dialog_change_picture);
        dialog.setCancelable(true);

        // Referencias a los botones
        Button btnDefaultImage = dialog.findViewById(R.id.btnDefaultImage);
        Button btnGallery = dialog.findViewById(R.id.btnGallery);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Acción para usar imagen predeterminada
        btnDefaultImage.setOnClickListener(v -> {
            FirebaseRealtimeDatabase.updateUserImage(mAuth.getUid(), DEFAULT_PROFILE_PICTURE, MainActivity.activityA);
            MainActivity.activityA.recreate();
            dialog.dismiss();
        });

        // Acción para abrir la galería
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            dialog.dismiss();
        });

        // Acción para cancelar
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Aquí puedes usar la URI para lo que necesites, por ejemplo, mostrar la imagen en un ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selectedImageUri);
                Bitmap croppedBitmap = cropToSquare(bitmap);
                imageProfile.setImageBitmap(croppedBitmap);
                imagenAñadida = true;
                new SubirImagenTask().execute(getImageUri(MainActivity.activityA, croppedBitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Para recortar la imagen y que tenga una forma cuadrada
    private Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = Math.min(width, height);
        int newHeight = Math.min(width, height);

        int cropW = (width - newWidth) / 2;
        int cropH = (height - newHeight) / 2;

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    private class SubirImagenTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            return CloudinaryDataBase.SaveImage(uris[0], MainActivity.activityA);
        }
        @Override
        protected void onPostExecute(String url) {
            // Aquí puedes manejar la URL de la imagen subida
            urlFoto =  url;

            if (urlFoto.startsWith("http://")) {
                urlFoto = urlFoto.replace("http://", "https://");
            }

            FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    FirebaseRealtimeDatabase.updateUserImage(user.getId(), urlFoto, MainActivity.activityA);
                }
                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        File imageFile = new File(context.getCacheDir(), "images");
        Uri imageUri = null;
        try {
            imageFile.mkdirs(); // make sure the directory exists
            File file = new File(imageFile, "image_" + System.currentTimeMillis() + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            imageUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUri;
    }

    private void dialogChangePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activityA, R.style.CustomAlertDialog);
        builder.setTitle("Confirmación");
        builder.setMessage("Se te enviará un enlace al correo");

        // Botón Confirmar
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = mAuth.getCurrentUser().getEmail();

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FirebaseAuth", "Correo de restablecimiento enviado a: " + email);
                                Toast.makeText(getContext(), "Correo enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("FirebaseAuth", "Error al enviar correo de restablecimiento", task.getException());
                                Toast.makeText(getContext(), "Error al enviar el correo de restablecimiento", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void leaveGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activityA, R.style.CustomAlertDialog);
        builder.setTitle("Abandonar grupo");
        builder.setMessage("¿Quieres abandonar el grupo?");

        // Botón Confirmar
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        FirebaseRealtimeDatabase.removeUserFromGroup(mAuth.getCurrentUser().getUid(), user.getGroupCode(), MainActivity.activityA);
                        MainActivity.activityA.recreate();
                    }
                    @Override
                    public void onFailure(Exception e) {
                    }
                });
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

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static TextView getNicknameUser() {
        return nicknameUser;
    }
}