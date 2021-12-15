package jgonzalezt.motorina.clases;

import java.io.Serializable;

public class EquipoLecturas implements Serializable {
    private Integer idLectura;
    private Integer idEquipo;
    private int lectura;
    private String date;

    public EquipoLecturas(Integer idLectura, Integer idEquipo, int lectura, String date) {
        this.idLectura = idLectura;
        this.idEquipo = idEquipo;
        this.lectura = lectura;
        this.date = date;
    }

    public Integer getIdLectura() {
        return idLectura;
    }

    public void setIdLectura(Integer idLectura) {
        this.idLectura = idLectura;
    }

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
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
}
