package com.example.gymbro.models;

public class SettingsModel {

    private String [] level;
    private String [] equipment;
    private String [] days;

    public SettingsModel(String[] level, String[] equipment, String[] days) {
        this.level = level;
        this.equipment = equipment;
        this.days = days;
    }
    public SettingsModel() {
    }

    public String[] getEquipment() {
        return equipment;
    }
    public void setEquipment(String[] equipment) {
        this.equipment = equipment;
    }

    public String[] getLevel() {
        return level;
    }
    public void setLevel(String[] level) {
        this.level = level;
    }

    public String[] getDays() {
        return days;
    }
    public void setDays(String[] days) {
        this.days = days;
    }

}
