package jgonzalezt.motorina;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jgonzalezt.motorina.activity.RegistroActivity;
import jgonzalezt.motorina.activity.RegistroEquipoActivity;
import jgonzalezt.motorina.adapter.AdapterEquipo;
import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.utility.Utility;

public class MainActivity extends AppCompatActivity {
    ArrayList<Equipo> listEquipo;
    RecyclerView recyclerView;
    AdapterEquipo adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_equipo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onClick(View view) {
        startActivity(new Intent(this, RegistroEquipoActivity.class));
    }

    private void llenarList() {
        listEquipo = Utility.loadDataEquipo(this);
        adapter = new AdapterEquipo(this, listEquipo);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new AdapterEquipo.OnItemClickListener() {
            @Override
            public void onItemEditClick(int position) {
                Equipo e = listEquipo.get(position);
                Intent i = new Intent(MainActivity.this, RegistroEquipoActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("equipo", e);
                i.putExtras(b);
                startActivity(i);
            }

            @Override
            public void onItemLecturasClick(int position) {
                Equipo e = listEquipo.get(position);
                Intent i = new Intent(MainActivity.this, RegistroActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("equipo", e);
                i.putExtras(b);
                startActivity(i);
            }

            @Override
            public void onItemDeleteClick(int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage(R.string.msg_delete_equipo)
                        .setPositiveButton(R.string.afirmativo, (dialog1, which) -> {
                            eliminarEquipo(listEquipo.get(position).getId());
                            llenarList();
                        })
                        .setNegativeButton(R.string.negativo, (dialog12, which) -> dialog12.dismiss());
                dialog.show();
            }
        });
    }

    private void eliminarEquipo(int id) {
        SQLiteDatabase db = Utility.conection(this).getWritableDatabase();
        String[] parameters = {String.valueOf(id)};

        db.delete(Utility.TABLE_EQUIPO, Utility.ID + "=?", parameters);
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        llenarList();
    }
}