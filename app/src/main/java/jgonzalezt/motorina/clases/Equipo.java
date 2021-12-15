package jgonzalezt.motorina.clases;

import java.io.Serializable;

public class Equipo implements Serializable {
    private int id;
    private String name;
    private String img;

    public Equipo() {
    }

    public Equipo(int id, String nombre, String img) {
        this.id = id;
        this.name = nombre;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
