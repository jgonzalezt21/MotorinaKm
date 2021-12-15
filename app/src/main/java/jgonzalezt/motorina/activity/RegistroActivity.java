package jgonzalezt.motorina.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.adapter.AdapterRegistro;
import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.clases.Registro;
import jgonzalezt.motorina.utility.Utility;
import jgonzalezt.motorina.utility.WRFile;

public class RegistroActivity extends AppCompatActivity {
    ArrayList<Registro> listRegistro;
    RecyclerView recyclerView;
    AdapterRegistro adapter;
    private Equipo equipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        setTitle(R.string.act_lectura);

        recyclerView = findViewById(R.id.rv_lecturas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle b = getIntent().getExtras();
        equipo = (Equipo) b.getSerializable("equipo");
    }

    @Override
    protected void onResume() {
        super.onResume();

        llenarList();
    }

    public void onClick(View view) {
        Intent i = new Intent(RegistroActivity.this, RegistroLecturaActivity.class);
        Bundle b = new Bundle();
        b.putInt("id_equipo", equipo.getId());
        i.putExtras(b);
        startActivity(i);
    }

    private void llenarList() {
        listRegistro = Utility.loadDataRegistro(this, equipo.getId());
        adapter = new AdapterRegistro(listRegistro);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new AdapterRegistro.OnItemClickListener() {
            @Override
            public void onItemEditClick(int position) {
                Registro l = listRegistro.get(position);
                Intent i = new Intent(RegistroActivity.this, RegistroLecturaActivity.class);
                Bundle b = new Bundle();
                b.putInt("id_equipo", equipo.getId());
                b.putSerializable("lectura", l);
                i.putExtras(b);
                startActivity(i);
            }

            @Override
            public void onItemDeleteClick(int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegistroActivity.this);
                dialog.setMessage(R.string.msg_delete_lectura)
                        .setPositiveButton(R.string.afirmativo, (dialog1, which) -> {
                            eliminarLectura(listRegistro.get(position).getId());
                            llenarList();
                        })
                        .setNegativeButton(R.string.negativo, (dialog12, which) -> dialog12.dismiss());
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.i_search) {
            if (listRegistro.size() > 1) {
                Intent i = new Intent(this, PromedioActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("equipo", equipo);
                i.putExtras(b);
                startActivity(i);
            } else {
                Toast.makeText(this, R.string.not_elemnts, Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.i_import) {//Recibir
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "text/plain" // .txt
            });
            someActivityResultLauncher.launch(i);
        } else {//Enviar
            if (listRegistro.size() > 0) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(R.string.msg_save_lecturas)
                        .setPositiveButton(R.string.afirmativo, (dialog1, which) -> {
                            Calendar c = Calendar.getInstance();
                            String dateTime = c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" +
                                    c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.HOUR) + "" + c.get(Calendar.MINUTE);
                            String fileName = String.format("%s_%s_backup.txt", dateTime, equipo.getName());

                            String[] data = new String[listRegistro.size()];
                            for (int i = 0; i < listRegistro.size(); i++) {
                                data[i] = listRegistro.get(i).toString();
                            }

                            WRFile.saveFile(this, fileName, data);
                        })
                        .setNegativeButton(R.string.negativo, (dialog12, which) -> dialog12.dismiss());
                dialog.show();
            } else {
                Toast.makeText(this, R.string.not_elemnts, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent d = result.getData();
                    assert d != null;
                    String path = d.getData().getPath().split(":")[1];
                    File f = new File(WRFile.pathExternalStorage.getPath() + "/" + path);

                    if (f.exists()) {
                        try {
                            if (f.length() < 10) {
                                throw new ArrayIndexOutOfBoundsException();
                            }

                            BufferedReader br = new BufferedReader(new FileReader(f));
                            String linea;
                            String lectura;
                            String date;
                            while ((linea = br.readLine()) != null) {
                                String[] temp = Registro.ofString(linea);
                                date = temp[0];
                                lectura = temp[1];
                                Utility.saveRegistro(RegistroActivity.this, lectura, date, equipo.getId(), -1);
                            }
                            br.close();
                            Toast.makeText(RegistroActivity.this, R.string.readFile, Toast.LENGTH_SHORT).show();
                        } catch (IOException | ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(this, R.string.file_not_compat, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.not_file, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void eliminarLectura(int id) {
        SQLiteDatabase db = Utility.conection(this).getWritableDatabase();
        String[] parameters = {String.valueOf(id)};

        db.delete(Utility.TABLE_REGISTRO, Utility.ID_REGISTRO + "=?", parameters);
        db.close();
    }
}