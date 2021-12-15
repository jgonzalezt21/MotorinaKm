package jgonzalezt.motorina.clases;

import java.io.Serializable;

public class Registro implements Serializable {
    private int id;
    private int lectura;
    private String date;

    public Registro() {
    }

    public Registro(int id, int lectura, String date) {
        this.id = id;
        this.lectura = lectura;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLectura() {
        return lectura;
    }

    public void setLectura(int lectura) {
        this.lectura = lectura;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String lecturaString() {
        return "" + lectura;
    }

    @Override
    public String toString() {
        return date + ";" + lectura;
    }

    public static String[] ofString(String line) {
        return line.split(";");
    }
}
