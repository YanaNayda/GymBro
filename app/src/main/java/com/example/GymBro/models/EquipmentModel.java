package com.example.GymBro.models;

import java.io.Serializable;

public class EquipmentModel implements Serializable {

    private String name;
    private int id;
    private boolean isSelected;

    public EquipmentModel(String name, int id) {
        this.name = name;
        this.id = id;
        this.isSelected=false;
    }
    public EquipmentModel() {
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int setId(int id) {
         this.id= id;
        return id;
    }

    public void setName(String name) {
       this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



}
