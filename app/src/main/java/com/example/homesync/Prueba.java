package com.example.homesync;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Prueba extends AppCompatActivity {

    private TableLayout tableLayout;
    private int numeroColumnas = 3;
    private String[][] datos = new String[40][numeroColumnas];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        tableLayout = findViewById(R.id.tableLayout);

        completarDatos();
        añadirDatosTabla();
    }

    private void completarDatos(){
        int dato = 1;

        for(int i = 0; i<datos.length; i++){
            for(int e = 0; e<numeroColumnas; e++){
                datos[i][e] = dato + "";
                dato++;
            }
        }
        Log.e("eee", datos[2][1]);
        Log.e("eee", datos.length+"");
    }

    private void añadirDatosTabla(){
        for(int i = 0; i<datos.length; i++){
            TableRow tableRow = new TableRow(this);

            for(int e = 0; e<numeroColumnas; e++){
                TextView textView = new TextView(this);
                textView.setText(datos[i][e]);

                // Aplicar el mismo estilo que la fila de encabezado
                textView.setTextSize(14);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(10, 10, 10, 10);
                textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

                // Usar LayoutParams para aplicar layout_weight
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4);
                textView.setLayoutParams(params);

                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }
    }
}