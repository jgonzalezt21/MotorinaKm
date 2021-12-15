package jgonzalezt.motorina.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.utility.Utility;

public class PromedioActivity extends AppCompatActivity implements TextWatcher {
    Equipo equipo;
    int lecturaMin, id_equipo;
    EditText et_lectura;
    TextView tv_km, tv_km_restante;
    ProgressBar pb;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promedio);

        setTitle(R.string.act_promedio);

        Bundle b = getIntent().getExtras();
        equipo = (Equipo) b.getSerializable("equipo");
        id_equipo = equipo.getId();
        lecturaMin = Utility.getLectura(this, id_equipo, "DESC");

        tv_km = findViewById(R.id.tv_km_promedio);
        tv_km_restante = findViewById(R.id.tv_km_restante);
        pb = findViewById(R.id.progressBar);

        et_lectura = findViewById(R.id.et_lectura_promed);
        et_lectura.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //NADA
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            int lectura = Integer.parseInt(s.toString());
            if (lectura > lecturaMin) {
                int km = lectura - lecturaMin;
                tv_km.setText(String.format("%sKm Recorridos", km));
                int km_promedio = Utility.kmPromedio(this, id_equipo);

                int porciento = km * 100 / km_promedio;
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(porciento);

                int km_restantes = km_promedio - km;
                String txt;
                if (km_restantes == 0) {
                    txt = "Cumplida la Media!";
                } else if (km_restantes > 0) {
                    txt = km_restantes + "Km Restantes";
                } else {
                    txt = km_restantes * -1 + "Km Superior a la Media!";
                }
                tv_km_restante.setText(txt);
            } else {
                clean();
            }
        } catch (NumberFormatException e) {
            clean();
        }
    }

    public void clean() {
        tv_km.setText("");
        tv_km_restante.setText("");
        pb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {
        //NADA
    }
}