package com.example.homesync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.imageview.ShapeableImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Prueba extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String urlFoto;
    private Uri selectedImageUri;
    private boolean imagenAñadida = false;

    private ShapeableImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);


        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageVieww);
        Button galeriaButton = findViewById(R.id.galeriaButton);
        Button camaraButton = findViewById(R.id.camaraButton);


        galeriaButton.setOnClickListener(v -> abrirGaleria());

        camaraButton.setOnClickListener(v -> {

        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Aquí puedes usar la URI para lo que necesites, por ejemplo, mostrar la imagen en un ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                Bitmap croppedBitmap = cropToSquare(bitmap);
                imageView.setImageBitmap(croppedBitmap);
                imagenAñadida = true;
                new SubirImagenTask().execute(getImageUri(Prueba.this, croppedBitmap));
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
            return CloudinaryDataBase.SaveImage(uris[0], Prueba.this);
        }
        @Override
        protected void onPostExecute(String url) {
            // Aquí puedes manejar la URL de la imagen subida
            urlFoto =  url;
            Log.e("dd", url);
            textView.setText(urlFoto);
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