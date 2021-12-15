package jgonzalezt.motorina.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.utility.Utility;

public class RegistroEquipoActivity extends AppCompatActivity {
    EditText et_name;
    ImageView img;
    private int id_equipo = -1;
    Equipo eq = null;
    String nueva_ruta_img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_equipo);

        setTitle(R.string.act_registro_equipo);

        et_name = findViewById(R.id.et_equipo);
        img = findViewById(R.id.iv_motorina);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            eq = (Equipo) b.getSerializable("equipo");
            id_equipo = eq.getId();
            et_name.setText(eq.getName());
            nueva_ruta_img = eq.getImg();
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the contacts-related task you need to do.
                if (nueva_ruta_img.isEmpty()) {
                    img.setImageResource(R.mipmap.ic_app);
                } else {
                    File f = new File(nueva_ruta_img);
                    if (f.exists()) {
                        Uri u = Uri.fromFile(f);
                        img.setImageURI(u);
                    }
                }
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                Toast.makeText(RegistroEquipoActivity.this, R.string.not_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint({"IntentReset", "QueryPermissionsNeeded"})
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.iv_motorina) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/");
            someActivityResultLauncher.launch(i);
        } else if (view.getId() == R.id.btn_save_equipo) {
            if (saveEquipo()) {
                finish();
            }
        } else {
            finish();
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent d = result.getData();
                    assert d != null;
                    Uri u = d.getData();
                    img.setImageURI(u);
                    nueva_ruta_img = u.getPath().substring(5);
                }
            });

    private boolean saveEquipo() {
        if (et_name.getText().length() > 0) {
            SQLiteDatabase db = Utility.conection(this).getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Utility.NAME, et_name.getText().toString());
            values.put(Utility.IMG, nueva_ruta_img);

            if (id_equipo != -1) {
                String[] parameters = {String.valueOf(id_equipo)};
                db.update(Utility.TABLE_EQUIPO, values, Utility.ID + "=?", parameters);
            } else {
                db.insert(Utility.TABLE_EQUIPO, null, values);
            }
            db.close();
            return true;
        } else {
            Toast.makeText(this, R.string.empty_camp, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}