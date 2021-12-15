package jgonzalezt.motorina.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.clases.Registro;
import jgonzalezt.motorina.utility.Utility;

import static java.lang.String.format;

public class AdapterRegistro extends RecyclerView.Adapter<AdapterRegistro.ViewHolderLectura> {
    ArrayList<Registro> listRegistro;
    private OnItemClickListener mListener;

    public AdapterRegistro(ArrayList<Registro> list) {
        listRegistro = list;
    }

    public interface OnItemClickListener {
        void onItemEditClick(int position);

        void onItemDeleteClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolderLectura onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_registro, parent, false);
        return new ViewHolderLectura(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderLectura holder, int position) {
        if (position < getItemCount() - 1) {
            long km = getKm(position);
            holder.tv_km.setText(format("+%s Km", km));
            int color;
            if (km > 60) {
                color = Color.BLUE;
            } else if (km > 40) {
                color = Color.GREEN;
            } else if (km > 20) {
                color = Color.YELLOW;
            } else {
                color = Color.RED;
            }
            holder.tv_km.setTextColor(color);

            DecimalFormat df = new DecimalFormat("+#.##$");
            holder.tv_ahorro.setText(df.format(km / Utility.km_promedio));

            holder.tv_date.setText(listRegistro.get(position).getDate() + format(" (+%s)", getDaysDates(position)));
        } else {
            holder.tv_km.setTextColor(Color.CYAN);
            holder.tv_km.setText(R.string.lectura_ini);
            holder.tv_ahorro.setText("");
            holder.tv_date.setText(listRegistro.get(position).getDate());
        }
        holder.tv_lectura.setText(listRegistro.get(position).lecturaString());
    }

    @Override
    public int getItemCount() {
        return listRegistro.size();
    }

    public int getKm(int position) {
        int act = listRegistro.get(position).getLectura();
        int sig = listRegistro.get(position + 1).getLectura();
        return act - sig;
    }

    public long getDaysDates(int position) {
        String now = listRegistro.get(position).getDate();
        String last = listRegistro.get(position + 1).getDate();
        return Utility.days(now, last);
    }

    public static class ViewHolderLectura extends RecyclerView.ViewHolder {
        TextView tv_km, tv_lectura, tv_date, tv_ahorro;
        ImageView iv_edit, iv_delete;

        public ViewHolderLectura(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tv_km = itemView.findViewById(R.id.tv_km);
            tv_lectura = itemView.findViewById(R.id.tv_lectura);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_ahorro = itemView.findViewById(R.id.tv_ahorro);
            iv_edit = itemView.findViewById(R.id.iv_edit_lecturas);
            iv_delete = itemView.findViewById(R.id.iv_delete_lectura);

            iv_edit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemEditClick(position);
                    }
                }
            });
            iv_delete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemDeleteClick(position);
                    }
                }
            });
        }
    }
}
