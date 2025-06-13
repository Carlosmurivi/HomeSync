package com.example.homesync.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

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

public class TasksFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View view;
    private ImageView btnInsertImage;
    private Bitmap imageBitmap;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private List<Task> allTask = new ArrayList<>();
    private List<String> tasksList = new ArrayList<>();
    private Task task = new Task();
    private String urlFoto;
    private Uri photoUri;
    private File photoFile;


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
        view = inflater.inflate(R.layout.activity_tasks_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        View separatingLine = view.findViewById(R.id.separatingLine);
        TextView textViewCompletedTasksUserTable = view.findViewById(R.id.textViewCompletedTasksTable);
        TableLayout tableLayout = view.findViewById(R.id.tableLayoutCompletedTask);
        TextView aviso = view.findViewById(R.id.textViewCreateGroup);
        Button createGroupButton = view.findViewById(R.id.buttonCreateGroup);
        Button joinGroupButton = view.findViewById(R.id.buttonJoinGroup);
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
                    textViewTitle.setVisibility(View.VISIBLE);
                    separatingLine.setVisibility(View.VISIBLE);
                    textViewCompletedTasksUserTable.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    // Configuración del click listener del FAB
                    fab.setOnClickListener(v -> completeTask());

                    Task.generateCompletedTaskTable(mAuth.getUid(), user, tableLayout, MainActivity.activityA);
                }
            }

            @Override
            public void onFailure(Exception e) {
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
                new TasksFragment.uploadCompleteTask().execute(CloudinaryDataBase.getImageUri(MainActivity.activityA, croppedBitmap));

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
}
