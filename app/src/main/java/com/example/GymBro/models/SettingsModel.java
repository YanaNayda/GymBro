package com.example.GymBro.models;

import java.util.List;

public class SettingsModel {

    private List<String> level;
    private List<String> equipment;
    private List<String> days;

    public SettingsModel(List<String> level, List<String> equipment, List<String> days) {
        this.level = level;
        this.equipment = equipment;
        this.days = days;
    }

    public SettingsModel() {
    }

    public List<String> getEquipment() {
        return equipment;
    }
    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public List<String> getLevel() {
        return level;
    }
    public void setLevel(List<String> level) {
        this.level = level;
    }

    public List<String> getDays() {
        return days;
    }
    public void setDays(List<String> days) {
        this.days = days;
    }
}

