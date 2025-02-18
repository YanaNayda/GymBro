package com.example.gymbro.models;

import java.io.Serializable;

public class Data  implements Serializable {

    private String name;
    private int drawable;
    private int id;

    public Data(String name, int drawable, int id) {
        this.name = name;
        this.drawable = drawable;
        this.id = id;
    }
    public Data() {
    }

    public String getName() {
        return name;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getId() {
        return id;
    }

    public int setId(int id) {
        return this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

}
