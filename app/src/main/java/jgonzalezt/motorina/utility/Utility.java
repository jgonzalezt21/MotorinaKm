package jgonzalezt.motorina.utility;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jgonzalezt.motorina.clases.Equipo;
import jgonzalezt.motorina.clases.Registro;

public class Utility {
    public static final String TABLE_EQUIPO = "equipo";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMG = "img";
    public static final String CREATE_TABLE_EQUIPO = "CREATE TABLE " +
            TABLE_EQUIPO + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + IMG + " TEXT)";
    public static final String DROP_TABLE_EQUIPO = "DROP TABLE IF EXISTS " + TABLE_EQUIPO;

    public static final String TABLE_REGISTRO = "lecturas";
    public static final String ID_REGISTRO = "id_lectura";
    public static final String ID_EQUIPO = "id_equipo";
    public static final String LECTURA = "lectura";
    public static final String DATE = "DATE";
    public static final String CREATE_TABLE_LECTURAS = "CREATE TABLE " +
            TABLE_REGISTRO + " (" + ID_REGISTRO + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LECTURA + " INTEGER, " + DATE + " DATE, " + ID_EQUIPO + " INTEGER)";
    public static final String DROP_TABLE_LECTURAS = "DROP TABLE IF EXISTS lecturas";

    public static final double km_promedio = 22;

    public static ConectionSQLHelper conection(Context ctx) {
        return new ConectionSQLHelper(ctx, "BD", null, 1);
    }

    public static ArrayList<Equipo> loadDataEquipo(Context ctx) {
        ArrayList<Equipo> list = new ArrayList<>();
        SQLiteDatabase db = conection(ctx).getReadableDatabase();

        Cursor cur = db.query(Utility.TABLE_EQUIPO, null, null, null, null, null, null);
        while (cur.moveToNext()) {
            Equipo e = new Equipo();
            e.setId(cur.getInt(0));
            e.setName(cur.getString(1));
            e.setImg(cur.getString(2));
            list.add(e);
        }
        cur.close();
        db.close();
        return list;
    }

    public static ArrayList<Registro> loadDataRegistro(Context ctx, int id_equipo) {
        ArrayList<Registro> list = new ArrayList<>();
        SQLiteDatabase db = conection(ctx).getReadableDatabase();
        String[] parameters = {String.valueOf(id_equipo)};

        Cursor cur = db.query(TABLE_REGISTRO, null, ID_EQUIPO + "=?", parameters, null, null, DATE + " DESC", null);
        while (cur.moveToNext()) {
            Registro l = new Registro();
            l.setId(cur.getInt(0));
            l.setLectura(cur.getInt(1));
            l.setDate(cur.getString(2));
            list.add(l);
        }
        cur.close();
        db.close();
        return list;
    }

    public static void saveRegistro(Context ctx, String lectura, String date, int id_equipo, int id_lectura) {
        SQLiteDatabase db = conection(ctx).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utility.LECTURA, Double.parseDouble(lectura));
        values.put(Utility.DATE, date);
        values.put(Utility.ID_EQUIPO, id_equipo);

        if (id_lectura != -1) {
            String[] parameters = {String.valueOf(id_lectura)};
            db.update(Utility.TABLE_REGISTRO, values, Utility.ID_REGISTRO + "=?", parameters);
        } else {
            db.insert(Utility.TABLE_REGISTRO, null, values);
        }
        db.close();
    }

    public static int getLectura(Context ctx, int id_equipo, String order) {
        SQLiteDatabase db = conection(ctx).getReadableDatabase();
        String[] parameters = {String.valueOf(id_equipo)};

        Cursor cur = db.query(TABLE_REGISTRO, null, ID_EQUIPO + "=?", parameters, null, null, DATE + " " + order, "1");
        cur.moveToFirst();
        int value = cur.getInt(1);
        cur.close();
        db.close();
        return value;
    }

    public static String getDate(Context ctx, int id_equipo, String order) {
        SQLiteDatabase db = conection(ctx).getReadableDatabase();
        String[] parameters = {String.valueOf(id_equipo)};

        Cursor cur = db.query(TABLE_REGISTRO, null, ID_EQUIPO + "=?", parameters, null, null, DATE + " " + order, "1");
        cur.moveToFirst();
        String value = cur.getString(2);
        cur.close();
        db.close();
        return value;
    }

    public static int cantCargas(Context ctx, int id_equipo) {
        SQLiteDatabase db = conection(ctx).getReadableDatabase();
        String[] cols = {"count(" + ID_REGISTRO + ")"};
        String[] parameters = {String.valueOf(id_equipo)};

        Cursor cur = db.query(TABLE_REGISTRO, cols, ID_EQUIPO + "=?", parameters, null, null, null, null);
        cur.moveToFirst();
        int value = cur.getInt(0);
        cur.close();
        db.close();
        return --value;//Descuento la lectura incial
    }

    public static int kmTotal(Context ctx, int id_equipo) {
        int lectIni = getLectura(ctx, id_equipo, "ASC");
        int lectFin = getLectura(ctx, id_equipo, "DESC");
        return lectFin - lectIni;
    }

    public static int kmPromedio(Context ctx, int id_equipo) {
        int size = cantCargas(ctx, id_equipo);
        int km = kmTotal(ctx, id_equipo);
        return km / size;
    }

    public static long days(String now, String last) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date dNow;
        Date dLast;
        try {
            dNow = df.parse(now);
            dLast = df.parse(last);
        } catch (ParseException e) {
            return 0;
        }
        return (dNow.getTime() - dLast.getTime()) / 86400000;
    }

    public static long daysTotal(Context ctx, int id_equipo) {
        String now = getDate(ctx, id_equipo, "DESC");
        String last = getDate(ctx, id_equipo, "ASC");
        return days(now, last);
    }

    public static int daysPromedio(Context ctx, int id_equipo) {
        long days = daysTotal(ctx, id_equipo);
        float size = cantCargas(ctx, id_equipo);
        return Math.round(days / size);
    }

    public static String twoDigits(int value) {
        return (value < 10) ? ("0" + value) : String.valueOf(value);
    }
}
