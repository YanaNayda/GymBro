package com.example.GymBro.models;

import java.util.ArrayList;
import java.util.List;

public class SettingsModel {

    private ArrayList<String> level;
    private ArrayList<String> equipment;
    private ArrayList<String> days;

    public SettingsModel(ArrayList<String> level, ArrayList<String> equipment, ArrayList<String> days) {
        this.level = level;
        this.equipment = equipment;
        this.days = days;
    }

    public SettingsModel() {
    }

    public ArrayList<String> getEquipment() {
        return equipment;
    }
    public void setEquipment(ArrayList<String> equipment) {
        this.equipment = equipment;
    }

    public ArrayList<String> getLevel() {
        return level;
    }
    public void setLevel(ArrayList<String> level) {
        this.level = level;
    }

    public ArrayList<String> getDays() {
        return days;
    }
    public void setDays(ArrayList<String> days) {
        this.days = days;
    }
}

