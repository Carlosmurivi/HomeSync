package com.example.homesync.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.Task;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class GroupFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_group_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();

        TextView textViewCodeTitle = view.findViewById(R.id.textViewCodeTitle);
        TextView textViewCode = view.findViewById(R.id.textViewCode);
        ImageButton imageButtonCopyCode = view.findViewById(R.id.imageButtonCopyCode);
        TextView textViewRankingTable = view.findViewById(R.id.textViewRankingTable);
        TableLayout tableLayoutRanking = view.findViewById(R.id.tableLayoutRanking);
        TextView textViewTasksTable = view.findViewById(R.id.textViewTasksTable);
        TableLayout tableLayoutTasks = view.findViewById(R.id.tableLayoutTasks);

        FloatingActionButton fabCreateTask = view.findViewById(R.id.fabCreateTask);

        TextView textViewCreateGroup = view.findViewById(R.id.textViewCreateGroup);
        Button buttonCreateGroup = view.findViewById(R.id.buttonCreateGroup);
        Button buttonJoinGroup = view.findViewById(R.id.buttonJoinGroup);


        FirebaseRealtimeDatabase.getUserById(mAuth.getCurrentUser().getUid(), MainActivity.activityA, new FirebaseRealtimeDatabase.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user.getGroupCode().trim().equals("")) {
                    textViewCreateGroup.setVisibility(View.VISIBLE);
                    buttonCreateGroup.setVisibility(View.VISIBLE);
                    buttonJoinGroup.setVisibility(View.VISIBLE);


                    buttonCreateGroup.setOnClickListener(v -> Group.createGroup(user.getId(), MainActivity.activityA));
                    buttonJoinGroup.setOnClickListener(v -> Group.joinGroup(MainActivity.activityA));
                } else {
                    textViewCodeTitle.setVisibility(View.VISIBLE);
                    textViewCode.setVisibility(View.VISIBLE);
                    imageButtonCopyCode.setVisibility(View.VISIBLE);
                    textViewRankingTable.setVisibility(View.VISIBLE);
                    tableLayoutRanking.setVisibility(View.VISIBLE);
                    textViewTasksTable.setVisibility(View.VISIBLE);
                    tableLayoutTasks.setVisibility(View.VISIBLE);

                    // Se comprueba si el usuario es administrador
                    if (user.isAdministrator()){
                        fabCreateTask.setVisibility(View.VISIBLE);

                        fabCreateTask.setOnClickListener(v -> Task.createTask(user.getId(), MainActivity.activityA));
                    }


                    textViewCode.setText(user.getGroupCode());
                    imageButtonCopyCode.setOnClickListener(v -> copyToClipboard(user.getGroupCode()));
                    Task.generateRankingTable(tableLayoutRanking, user, getContext());
                    Task.generateAllTaskTable(tableLayoutTasks, user, getContext());
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
     * Copia el texto pasado por parámetros en el portapapeles
     * @param text
     */
    private void copyToClipboard(String text){
        ClipboardManager clipboard = (ClipboardManager) MainActivity.activityA.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(MainActivity.activityA, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
}
