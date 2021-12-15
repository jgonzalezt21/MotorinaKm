package jgonzalezt.motorina.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import jgonzalezt.motorina.R;
import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.clases.Registro;
import jgonzalezt.motorina.utility.Utility;

import static java.lang.String.format;

public class AdapterEquipo extends RecyclerView.Adapter<AdapterEquipo.ViewHolderEquipo> {
    ArrayList<Equipo> listEquipo;
    private OnItemClickListener mListener;
    private final Context ctx;
    private int id_equipo;

    public interface OnItemClickListener {
        void onItemEditClick(int position);

        void onItemLecturasClick(int position);

        void onItemDeleteClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdapterEquipo(Context ctx, ArrayList<Equipo> listEquipo) {
        this.listEquipo = listEquipo;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolderEquipo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_equipo, parent, false);
        return new ViewHolderEquipo(v, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderEquipo holder, int position) {
        holder.tv_name_equipo.setText(listEquipo.get(position).getName());
        ArrayList<Registro> list = Utility.loadDataRegistro(ctx, listEquipo.get(position).getId());
        id_equipo = listEquipo.get(position).getId();
        if (list.size() > 1) {
            int km_total = Utility.kmTotal(ctx, id_equipo);
            holder.tv_recorrido.setText(format("Recorrido: %s Km en %s", km_total, date()));
            int[] kmMaxMin = getKmMaxMin(list);
            holder.tv_rec_max.setText(format("Máximo: %s Km - Mínimo %s Km", kmMaxMin[0], kmMaxMin[1]));
            holder.tv_autonomia.setText(format("Autonomía: %s Km cada %s Días", Utility.kmPromedio(ctx, id_equipo), Utility.daysPromedio(ctx, id_equipo)));
            holder.tv_total_cargas.setText(format("Total de Cargas: %s - Ahorro: %s $", Utility.cantCargas(ctx, id_equipo), Math.round(km_total / Utility.km_promedio)));
        }

        if (listEquipo.get(position).getImg().isEmpty()) {
            holder.iv_equipo.setImageResource(R.mipmap.ic_app);
        } else {
            File f = new File(listEquipo.get(position).getImg());
            if (f.exists()) {
                Uri u = Uri.fromFile(f);
                holder.iv_equipo.setImageURI(u);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listEquipo.size();
    }

    private String date() {
        String result;
        long days = Utility.daysTotal(ctx, id_equipo);
        long meses = days / 30;
        long dayMeses = days % 30;
        long years = meses / 12;
        long mesesYears = meses % 12;

        if (days < 31) {//Dias
            result = days + " Días";
        } else if (meses < 12) {//Meses
            if (meses > 1) {
                result = meses + " Meses";
            } else {
                result = meses + " Mes";
            }

            if (dayMeses != 0) {
                if (dayMeses > 1) {
                    result += " y " + dayMeses + " Días";
                } else {
                    result += " y " + dayMeses + " Día";
                }
            }
        } else {//Años
            if (years > 1) {
                result = years + " Años";
            } else {
                result = years + " Año";
            }

            if (mesesYears != 0) {
                if (mesesYears > 1) {
                    result += " y " + mesesYears + " Meses";
                } else {
                    result += " y " + mesesYears + " Mese";
                }
            }
        }
        return result;
    }

    private int[] getKmMaxMin(ArrayList<Registro> list) {
        int kmMax = 0, kmMin = 1000;
        int last = list.get(0).getLectura();
        for (int i = 1; i < list.size(); i++) {
            int now = list.get(i).getLectura();
            int temp_km = last - now;
            last = now;
            if (temp_km > kmMax) {
                kmMax = temp_km;
            } else if (temp_km < kmMin) {
                kmMin = temp_km;
            }
        }
        return new int[]{kmMax, kmMin};
    }

    public static class ViewHolderEquipo extends RecyclerView.ViewHolder {
        TextView tv_name_equipo, tv_recorrido, tv_rec_max, tv_autonomia, tv_total_cargas;
        ImageView iv_equipo, iv_edit_equipo, iv_lecturas_equipo, iv_delete_equipo;

        public ViewHolderEquipo(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tv_name_equipo = itemView.findViewById(R.id.tv_name_equipo);
            tv_recorrido = itemView.findViewById(R.id.tv_recorrido);
            tv_rec_max = itemView.findViewById(R.id.tv_recorrido_max);
            tv_autonomia = itemView.findViewById(R.id.tv_autonomia);
            tv_total_cargas = itemView.findViewById(R.id.tv_total);
            iv_equipo = itemView.findViewById(R.id.iv_equipo);
            iv_edit_equipo = itemView.findViewById(R.id.iv_edit_equipo);
            iv_lecturas_equipo = itemView.findViewById(R.id.iv_lecturas_equipo);
            iv_delete_equipo = itemView.findViewById(R.id.iv_delete_equipo);

            iv_edit_equipo.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemEditClick(position);
                    }
                }
            });
            iv_lecturas_equipo.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLecturasClick(position);
                    }
                }
            });
            iv_delete_equipo.setOnClickListener(v -> {
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
