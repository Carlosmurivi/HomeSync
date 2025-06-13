package com.example.homesync;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.homesync.Model.PurchaseProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.sun.mail.imap.protocol.Item;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<PurchaseProduct> items;
    private FirebaseAuth mAuth;
    private OnDataChangedListener dataChangedListener;

    public CustomAdapter(Context context, List<PurchaseProduct> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mAuth = FirebaseAuth.getInstance();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_purchase_product, parent, false);
        }
        TextView titleTextView = convertView.findViewById(R.id.itemTitle);
        ImageButton favoriteTrueImageButton = convertView.findViewById(R.id.favoriteTrueImageButton);
        ImageButton favoriteImageButton = convertView.findViewById(R.id.favoriteImageButton);
        ImageButton deleteImageButton = convertView.findViewById(R.id.deleteImageButton);
        PurchaseProduct item = items.get(position);

        if (item.isBought()) {
            Log.e("compradoo", item.getTitle() + " Tachado");
            titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleTextView.setTextColor(Color.parseColor("#555555"));
        } else {
            titleTextView.setPaintFlags(titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            titleTextView.setTextColor(ContextCompat.getColor(context, R.color.texts));
        }

        if (item.isFavorite()) {
            titleTextView.setVisibility(View.VISIBLE);
            favoriteTrueImageButton.setVisibility(View.VISIBLE);
            favoriteImageButton.setVisibility(View.GONE);
            deleteImageButton.setVisibility(View.GONE);

            // Eliminar de favoritos
            favoriteTrueImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PurchaseProduct.addToNotFavorites(mAuth.getUid(), item.getId(), MainActivity.activityA);
                    if (dataChangedListener != null) {
                        dataChangedListener.onDataChanged();
                    }
                }
            });
        } else {
            titleTextView.setVisibility(View.VISIBLE);
            favoriteTrueImageButton.setVisibility(View.GONE);
            favoriteImageButton.setVisibility(View.VISIBLE);
            deleteImageButton.setVisibility(View.VISIBLE);

            // Añadir a favoritos
            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PurchaseProduct.addToFavorites(mAuth.getUid(), item.getId(), MainActivity.activityA);
                    if (dataChangedListener != null) {
                        dataChangedListener.onDataChanged();
                    }
                }
            });

            // Eliminar item
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseRealtimeDatabase.removePurchaseProduct(mAuth.getUid(), item.getId(), MainActivity.activityA);
                    items.remove(position);
                    notifyDataSetChanged();
                }
            });
        }



        // Añadir o quitar de comprado
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((titleTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == 0) {
                    PurchaseProduct.addToBought(mAuth.getUid(), item.getId(), MainActivity.activityA);
                    if (dataChangedListener != null) {
                        dataChangedListener.onDataChanged();
                    }
                } else {
                    PurchaseProduct.addToNotBought(mAuth.getUid(), item.getId(), MainActivity.activityA);
                    if (dataChangedListener != null) {
                        dataChangedListener.onDataChanged();
                    }
                }
            }
        });

        titleTextView.setText(item.getTitle());

        return convertView;
    }

    public interface OnDataChangedListener {
        void onDataChanged(); // Esto se llamará cuando haya que recargar la lista
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.dataChangedListener = listener;
    }
}
