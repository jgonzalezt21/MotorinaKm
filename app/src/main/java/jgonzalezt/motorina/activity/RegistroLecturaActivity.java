package jgonzalezt.motorina.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.clases.DatePickerFragment;
import jgonzalezt.motorina.clases.Registro;
import jgonzalezt.motorina.utility.Utility;

public class RegistroLecturaActivity extends AppCompatActivity {
    EditText et_lectura;
    TextView tv_date;
    private int id_lectura = -1;
    private int id_equipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_registro);

        setTitle(R.string.act_registro_lectura);

        et_lectura = findViewById(R.id.et_lectura);
        tv_date = findViewById(R.id.tv_date_date);

        Bundle b = getIntent().getExtras();
        id_equipo = b.getInt("id_equipo");
        Registro l = (Registro) b.getSerializable("lectura");
        if (l != null) {
            id_lectura = l.getId();
            et_lectura.setText(String.valueOf(l.getLectura()));
            tv_date.setText(l.getDate());
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.tv_date_date) {
            @SuppressLint("SetTextI18n") DatePickerFragment dialog = DatePickerFragment.newInstance((view1, year, month, dayOfMonth) ->
                    tv_date.setText(year + "/" + Utility.twoDigits(month + 1) + "/" + Utility.twoDigits(dayOfMonth)));
            dialog.show(getSupportFragmentManager(), "datePicker");
        } else if (view.getId() == R.id.btn_save_lectura) {
            if (et_lectura.getText().length() > 0 && !tv_date.getText().equals("Fecha")) {
                Utility.saveRegistro(this, et_lectura.getText().toString(), tv_date.getText().toString(), id_equipo, id_lectura);
                finish();
            } else {
                Toast.makeText(this, R.string.empty_camp, Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
    }
}