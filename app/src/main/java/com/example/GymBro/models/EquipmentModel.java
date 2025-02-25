package com.example.GymBro.models;

import java.io.Serializable;

public class EquipmentModel implements Serializable {

    private final String name; // Make final if immutable
    private final int id;      // Make final if immutable
    private boolean isSelected;

    public EquipmentModel(String name, int id) {
        this.name = name;
        this.id = id;
        this.isSelected = false; // Default to false
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "EquipmentModel{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", isSelected=" + isSelected +
                '}';
    }
}