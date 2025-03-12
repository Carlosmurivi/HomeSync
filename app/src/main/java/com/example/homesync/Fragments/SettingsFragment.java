package com.example.homesync.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homesync.CloudinaryDataBase;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.Index;
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

public class SettingsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String urlFoto;
    private Uri selectedImageUri;
    private boolean imagenAñadida = false;
    private FirebaseAuth mAuth;
    private ImageView imageProfile;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings_fragment, container, false);


        mAuth = FirebaseAuth.getInstance();

        imageProfile = view.findViewById(R.id.imageProfile);
        TextView nicknameUser = view.findViewById(R.id.nicknameUser);
        TextView mailUser = view.findViewById(R.id.mailUser);
        ImageButton changeNickname = view.findViewById(R.id.changeNickname);
        Button changePassword = view.findViewById(R.id.changePassword);
        Button leaveGroup = view.findViewById(R.id.leaveGroup);
        Button logOut = view.findViewById(R.id.logOut);


        TextView createGroupTextView = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);

        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if(!user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    createGroupTextView.setVisibility(View.VISIBLE);
                } else {
                    imageProfile.setVisibility(View.VISIBLE);
                    nicknameUser.setVisibility(View.VISIBLE);
                    mailUser.setVisibility(View.VISIBLE);
                    changeNickname.setVisibility(View.VISIBLE);
                    changePassword.setVisibility(View.VISIBLE);
                    leaveGroup.setVisibility(View.VISIBLE);
                    logOut.setVisibility(View.VISIBLE);

                    Glide.with(SettingsFragment.this)
                            .load(user.getImage())
                            .skipMemoryCache(true) // Evita la caché en RAM
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Evita la caché en disco
                            .into(imageProfile);
                    Glide.with(SettingsFragment.this).load(user.getImage()).into(imageProfile);
                    nicknameUser.setText(user.getNickname());
                    mailUser.setText("@" + user.getMail());
                }
            }
            @Override
            public void onFailure(Exception e) {

            }
        });




        // Cerrar Sesión
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.activityA.finish();
                startActivity(new Intent(MainActivity.activityA, Index.class));
            }
        });

        // Cambiar Imagen de Perfil
        imageProfile.setOnClickListener(v -> abrirGaleria());

        // Cambiar Apodo
        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.activityA, "¡Editar Apodo!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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
}