package com.example.homesync.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.homesync.CustomAdapter;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.MainActivity;
import com.example.homesync.Model.Group;
import com.example.homesync.Model.PurchaseProduct;
import com.example.homesync.Model.User;
import com.example.homesync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.sun.mail.imap.protocol.Item;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View view;
    private ListView listView;
    private CustomAdapter adapter;
    private List<PurchaseProduct> items;
    private String producto = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_shopping_list_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();

        TextView textViewTitle = view.findViewById(R.id.shoppingListTitle);
        View separatingLine = view.findViewById(R.id.separatingLine);
        TextView shoppingListTitle = view.findViewById(R.id.shoppingListTitle);
        listView = view.findViewById(R.id.listView);
        TextView aviso = view.findViewById(R.id.createGroupTextView);
        Button createGroupButton = view.findViewById(R.id.createGroupButton);
        Button joinGroupButton = view.findViewById(R.id.joinGroupButton);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        FloatingActionButton fabRestartPurchase = view.findViewById(R.id.fabRestartPurchase);


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
                    shoppingListTitle.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    fabRestartPurchase.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);

                    // LLenar la lista con datos de ejemplo
                    items = new ArrayList<>();

                    PurchaseProduct.getPurchaseProducts(items, user.getGroupCode(), MainActivity.activityA);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // Configurar el adaptador personalizado
                        adapter = new CustomAdapter(MainActivity.activityA, items);
                        listView.setAdapter(adapter);

                        // Escucha cambios desde el adaptador
                        adapter.setOnDataChangedListener(new CustomAdapter.OnDataChangedListener() {
                            @Override
                            public void onDataChanged() {
                                recargarProductos(user.getGroupCode());
                            }
                        });

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PurchaseProduct.createPurchaseProduct(user.getGroupCode(), adapter, items, MainActivity.activityA);
                            }
                        });

                        fabRestartPurchase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //PurchaseProduct.createPurchaseProduct(user.getGroupCode(), adapter, items, MainActivity.activityA);
                            }
                        });
                    }, 500); // 1000 milisegundos = 1 segundo
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error al generar los elementos", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void recargarProductos(String groupCode) {
        items.clear(); // Limpia la lista antes de llenarla
        PurchaseProduct.getPurchaseProducts(items, groupCode, MainActivity.activityA);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            adapter.notifyDataSetChanged();
        }, 300);
    }
}