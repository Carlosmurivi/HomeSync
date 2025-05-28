package com.example.homesync.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.example.homesync.CloudinaryDataBase;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static String[] topCompletedTaskTable = {"Usuario", "Descripción", "Puntos", "Fecha", "false", "imagen"};
    private Uri photoUri;
    private File photoFile;
    private View view;


    private FirebaseAuth mAuth;
    private ImageView btnInsertImage;
    private Bitmap imageBitmap;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private List<Task> allTask = new ArrayList<>();
    private List<String> tasksList = new ArrayList<>();
    private Task task = new Task();
    private String urlFoto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (photoUri != null) {
                            try {
                                // 1. Cargar el bitmap original desde el archivo
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri);

                                // 2. Leer orientación EXIF
                                ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                int rotationDegrees = 0;

                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotationDegrees = 90;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotationDegrees = 180;
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotationDegrees = 270;
                                        break;
                                }

                                // 3. Rotar el bitmap si es necesario
                                Matrix matrix = new Matrix();
                                matrix.postRotate(rotationDegrees);
                                imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                                if (btnInsertImage != null) {
                                    btnInsertImage.setImageBitmap(CloudinaryDataBase.cropToSquare(imageBitmap));
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error al cargar imagen rotada", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
        );

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();

        ImageView image = view.findViewById(R.id.imageView);
        TextView nickName = view.findViewById(R.id.textViewNicknameUser);
        TextView points = view.findViewById(R.id.textViewPoints);
        ImageView imageGoldMedal = view.findViewById(R.id.imageGoldMedal);
        TextView goldMedal = view.findViewById(R.id.goldMedal);
        ImageView imageSilverMedal = view.findViewById(R.id.imageSilverMedal);
        TextView silverMedal = view.findViewById(R.id.silverMedal);
        ImageView imageBronzeMedal = view.findViewById(R.id.imageBronzeMedal);
        TextView bronzeMedal = view.findViewById(R.id.bronzeMedal);
        TextView textViewCompletedTasksUserTable = view.findViewById(R.id.textViewCompletedTasksUserTable);
        TableLayout tableLayout = view.findViewById(R.id.tableLayoutCompletedTaskUser);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        Button joinGroupButton = view.findViewById(R.id.joinGroupButton);
        FloatingActionButton fab = view.findViewById(R.id.fab);


        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user.getGroupCode().trim().equals("")) {
                    createGroupButton.setVisibility(View.VISIBLE);
                    joinGroupButton.setVisibility(View.VISIBLE);
                    aviso.setVisibility(View.VISIBLE);

                    createGroupButton.setOnClickListener(v -> Group.createGroup(user.getId(), MainActivity.activityA));
                    joinGroupButton.setOnClickListener(v -> Group.joinGroup(MainActivity.activityA));
                } else {
                    image.setVisibility(View.VISIBLE);
                    nickName.setVisibility(View.VISIBLE);
                    points.setVisibility(View.VISIBLE);
                    imageGoldMedal.setVisibility(View.VISIBLE);
                    goldMedal.setVisibility(View.VISIBLE);
                    imageSilverMedal.setVisibility(View.VISIBLE);
                    silverMedal.setVisibility(View.VISIBLE);
                    imageBronzeMedal.setVisibility(View.VISIBLE);
                    bronzeMedal.setVisibility(View.VISIBLE);
                    textViewCompletedTasksUserTable.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    if (isAdded() && getActivity() != null) {
                        Glide.with(requireContext()).load(user.getImage()).into(image);
                    }

                    // Configuración del click listener del FAB
                    fab.setOnClickListener(v -> completeTask());
                    nickName.setText(user.getNickname());
                    points.setText(Integer.toString(user.getPoints()));
                    goldMedal.setText(Integer.toString(user.getGoldMedals()));
                    silverMedal.setText(Integer.toString(user.getSilverMedals()));
                    bronzeMedal.setText(Integer.toString(user.getBronzeMedals()));

                    Task.generateCompletedTasksUserTable(tableLayout, user.getId(), MainActivity.activityA);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Error", "Error al generar los elementos");
                Toast.makeText(getContext(), "Error al generar los elementos", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    /**
     * Completa una tarea
     */
    @SuppressLint("NewApi")
    private void completeTask() {
        // Crear el diálogo
        Dialog dialog = new Dialog(MainActivity.activityA);
        dialog.setContentView(R.layout.activity_dialog_complete_task);
        dialog.setCancelable(true);

        // Referencias a los botones
        Spinner spinnerTask = dialog.findViewById(R.id.spinnerTask);
        btnInsertImage = dialog.findViewById(R.id.imageTask);
        btnInsertImage.setImageResource(R.drawable.fondo_x);
        Button btnCompleteTask = dialog.findViewById(R.id.completeButton);
        Button btnCancel = dialog.findViewById(R.id.backButton);


        // Cargamos las tareas al spinner
        getTasks(dialog);


        // Acción para añadir la fotografia
        btnInsertImage.setOnClickListener(v1 -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                try {
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".fileprovider",
                                photoFile
                        );
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        cameraLauncher.launch(intent);
                        Toast.makeText(getContext(), "Abriendo cámara...", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(getContext(), "No se pudo crear el archivo", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Acción para crear la tarea
        btnCompleteTask.setOnClickListener(v1 -> {
            // Se recogen todos los datos, se comprueba que todo este completo, y se crea una nueva tarea
            if (spinnerTask.getSelectedItem().toString() != null && !spinnerTask.getSelectedItem().toString().equals("Selecciona una tarea")) {

                for (Task t : allTask) {
                    if (t.getDescription().equals(spinnerTask.getSelectedItem().toString())) {
                        task = t;
                        task.setUserId(mAuth.getUid());
                        LocalDateTime now = LocalDateTime.now();
                        task.setDateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        break;
                    }
                }

                Bitmap croppedBitmap = CloudinaryDataBase.cropToSquare(imageBitmap);
                new ProfileFragment.uploadCompleteTask().execute(CloudinaryDataBase.getImageUri(MainActivity.activityA, croppedBitmap));

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción para cancelar
        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }


    /**
     * Se encarga de cargar todas las tareas en el spinner para poder seleccionarla al completar una de ellas.
     *
     * @param dialog
     */
    private void getTasks(Dialog dialog) {
        Spinner spinnerTask = dialog.findViewById(R.id.spinnerTask);
        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getTasksByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.TasksCallback() {
                    @Override
                    public ArrayList<String[]> onTasksLoaded(List<Task> tasks) {
                        allTask = tasks;
                        // Se limpia el array y se le añade el valor predeterminado.
                        tasksList = new ArrayList<>();
                        tasksList.add("Selecciona una tarea");

                        for (Task task : tasks) {

                            tasksList.add(task.getDescription());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.activityA, R.layout.spinner_item, tasksList);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        spinnerTask.setAdapter(adapter);
                        return null;
                    }

                    @Override
                    public void onError(DatabaseError error) {
                        Toast.makeText(MainActivity.activityA, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Guarda la imagen y la añade su url a la tarea.
     * También almacena la tarea en la base de datos.
     */
    private class uploadCompleteTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            return CloudinaryDataBase.SaveImage(uris[0], MainActivity.activityA);
        }

        @Override
        protected void onPostExecute(String url) {
            // Aquí puedes manejar la URL de la imagen subida
            urlFoto = url;

            if (urlFoto.startsWith("http://")) {
                urlFoto = urlFoto.replace("http://", "https://");
            }

            task.setImageUrl(urlFoto);


            FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    FirebaseRealtimeDatabase.completeTask(mAuth.getUid(), task, user.getGroupCode(), MainActivity.activityA);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Crea una imagen temporal para poder almacenarla con buena calidad.
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }












    /**
     * Genera una tabla y la completa con los datos.
     */
    private void generateCompletedTaskTable() {
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        ArrayList<String[]> datos = new ArrayList<>();
        datos.add(topCompletedTaskTable);

        completeCompletedTaskTable(datos);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Limpia la tabla antes de añadir filas nuevas
            tableLayout.removeAllViews();
            tableLayout.setPadding(8, 8, 8, 8);

            boolean firstRow = true;
            for (String[] row : datos) {
                TableRow fila = new TableRow(MainActivity.activityA);

                for (int j = 0; j < (row.length) - 2; j++) {
                    TextView celda = new TextView(MainActivity.activityA);
                    celda.setText(row[j]);
                    celda.setPadding(16, 16, 16, 16);
                    celda.setTextSize(16);

                    // Comprobamos si es una tarea predeterminada para cambiarle el color del texto
                    if (row[(row.length) - 2].equals("true")) {
                        celda.setTextColor(ContextCompat.getColor(MainActivity.activityA, R.color.darkBlue));
                    } else {
                        celda.setTextColor(ContextCompat.getColor(MainActivity.activityA, R.color.texts));
                    }

                    celda.setBackgroundColor(ContextCompat.getColor(MainActivity.activityA, R.color.background));

                    // Establecer márgenes entre celdas
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(4, 4, 4, 4); // izquierda, arriba, derecha, abajo
                    celda.setLayoutParams(params);

                    if (firstRow) { // Primera fila = encabezado
                        celda.setTextColor(ContextCompat.getColor(MainActivity.activityA, R.color.black));
                        celda.setBackgroundColor(ContextCompat.getColor(MainActivity.activityA, R.color.colorPrimary));
                        celda.setTypeface(null, Typeface.BOLD);
                    } else {
                        // Listener solo para las celdas que no son de encabezado
                        celda.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayImageDialog(row[(row.length)-1]);
                                //Toast.makeText(MainActivity.activityA, row[(row.length)-2], Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    fila.addView(celda);
                }

                firstRow = false;
                tableLayout.addView(fila);
            }
        }, 1500); // 1000 milisegundos = 1 segundo
    }


    /**
     * Este metodo rellena la tabla con datos, mostrando un ranking con el puesto, nombre y puntos totales.
     *
     * @param datos
     * @return Lista de registros para imprimir en la tabla.
     */
    private ArrayList<String[]> completeCompletedTaskTable(ArrayList<String[]> datos) {
        FirebaseRealtimeDatabase.getUserById(mAuth.getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                FirebaseRealtimeDatabase.getCompletedTasksByGroupId(user.getGroupCode(), new FirebaseRealtimeDatabase.TasksCallback() {
                    @Override
                    public ArrayList<String[]> onTasksLoaded(List<Task> tasks) {
                        for (Task t : tasks) {
                            FirebaseRealtimeDatabase.getUserById(t.getUserId(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    String[] task = {user.getNickname(), t.getDescription(), Integer.toString(t.getPoints()), t.getDateTime(), Boolean.toString(t.isPredetermined()), t.getImageUrl()};
                                    datos.add(task);
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        return datos;
                    }

                    @Override
                    public void onError(DatabaseError error) {
                        Toast.makeText(MainActivity.activityA, "Error al obtener las tareas", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.activityA, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        return datos;
    }


    /**
     * Genera un Dialog mostrando la imagen que se le pase como url por parámetros.
     * @param imageUrl
     */
    private void displayImageDialog(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.activity_dialog_imagen, null);

        ImageView imageView = view.findViewById(R.id.imageView);

        // Cargar imagen desde la URL usando Glide
        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.fondo_x) // Opcional: imagen mientras carga
                //.error(R.drawable.error_image)       // Opcional: imagen si falla
                .into(imageView);

        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }









    // Ordenar correctamente los elementos de ProfileFragment

    // Mandarlo al git

    // Cuando una tarea no predeterminada se complete se elimina





    // Que cada mes o semana se reseteen los puntos y que se entreguen medallas de oro, plata y bronce


    


    // Crear la lista de la compra

    // Incorporar unas tareas predeterminadas al grupo (Pasar aspiradora, Limpiar los baños, Recoger la habitación, Tender la ropa, Recoger lavavajillas, Hacer la compra)
}