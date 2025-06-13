package com.example.homesync.Model;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homesync.CustomAdapter;
import com.example.homesync.FirebaseRealtimeDatabase;
import com.example.homesync.MainActivity;
import com.example.homesync.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class PurchaseProduct {

    // ATRIBUTOS
    private String id;
    private String title;
    private boolean favorite;
    private boolean bought;





    // CONSTRUCTORES
    public PurchaseProduct(){
    }

    public PurchaseProduct(String title, boolean favorite, boolean bought){
        this.title = title;
        this.favorite = favorite;
        this.bought = bought;
    }

    public PurchaseProduct(String id, String title, boolean favorite, boolean bought){
        this.id = id;
        this.title = title;
        this.favorite = favorite;
    }





    // GETTERS AND SETTERS
    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isBought() {
        return bought;
    }





    // METODOS
    public static void addToFavorites(String userId, String purchaseProductId, Context context){
        FirebaseRealtimeDatabase.addPurchaseProductToFavorites(userId, purchaseProductId, context);
    }

    public static void addToNotFavorites(String userId, String purchaseProductId, Context context){
        FirebaseRealtimeDatabase.addPurchaseProductToNotFavorites(userId, purchaseProductId, context);
    }

    public static void addToBought(String userId, String purchaseProductId, Context context){
        FirebaseRealtimeDatabase.addPurchaseProductToBought(userId, purchaseProductId, context);
    }

    public static void addToNotBought(String userId, String purchaseProductId, Context context){
        FirebaseRealtimeDatabase.addPurchaseProductToNotBought(userId, purchaseProductId, context);
    }

    public static List<PurchaseProduct> getPurchaseProducts(List<PurchaseProduct> items, String groupId, Context context){
        FirebaseRealtimeDatabase.getPurchaseProductToGroup(groupId, new FirebaseRealtimeDatabase.PurchaseProductCallback() {
            @Override
            public ArrayList<String[]> onPurchaseProductLoaded(List<PurchaseProduct> purchaseProducts) {
                //items = purchaseProducts;
                for (PurchaseProduct purchaseProduct: purchaseProducts){
                    items.add(purchaseProduct);
                }
                return null;
            }

            @Override
            public void onError(DatabaseError error) {
                Toast.makeText(context, "Error al obtener los productos de la tabla", Toast.LENGTH_SHORT).show();
            }
        });
        return items;
    }

    public static void createPurchaseProduct(String groupId, CustomAdapter adapter, List<PurchaseProduct> items, Context context){
        // Crear el diálogo
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_create_purchase_product);
        dialog.setCancelable(true);

        // Referencias a los botones
        TextView purchaseProductTitle = dialog.findViewById(R.id.purchaseProductTitle);
        TextInputEditText purchaseProductEditText = dialog.findViewById(R.id.purchaseProductEditText);
        TextInputLayout purchaseProductLayout = dialog.findViewById(R.id.purchaseProductLayout);
        Button btnCompleteTask = dialog.findViewById(R.id.purchaseProductCreateButton);
        Button btnCancel = dialog.findViewById(R.id.purchaseProductBackButton);


        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String producto = String.valueOf(purchaseProductEditText.getText());
                if(producto != null && !producto.trim().equals("")) {
                    PurchaseProduct purchaseProduct = new PurchaseProduct(producto, false, false);

                    FirebaseRealtimeDatabase.addPurchaseProductToGroup(groupId, purchaseProduct, context);

                    items.add(purchaseProduct);

                    purchaseProductEditText.setText("");
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }
}
