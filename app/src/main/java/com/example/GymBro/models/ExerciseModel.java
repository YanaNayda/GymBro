package com.example.GymBro.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExerciseModel implements Serializable {
    String name;
    String level;
    String equipment;
    String category;
    ArrayList<String> primaryMuscles;
    ArrayList<String> secondaryMuscles;
    String instructions;
    String img0;
    String img1;
    private boolean isSelected;

    public ExerciseModel(String name, String level, String equipment, String category, ArrayList<String> primaryMuscles, ArrayList<String> secondaryMuscles, String instructions, String img0, String img1, boolean isSelected) {
        this.name = name;
        this.level = level;
        this.equipment = equipment;
        this.category = category;
        this.primaryMuscles = primaryMuscles;
        this.secondaryMuscles = secondaryMuscles;
        this.instructions = instructions;
        this.img0 = img0;
        this.img1 = img1;
        this.isSelected = isSelected;
    }

    public ExerciseModel(String name) {
        this.name = name;

    }
    public ExerciseModel() {
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<String> getPrimaryMuscles() {
        return primaryMuscles;
    }

    public void setPrimaryMuscles(ArrayList<String> primaryMuscles) {
        this.primaryMuscles = primaryMuscles;
    }

    public List<String> getSecondaryMuscles() {
        return secondaryMuscles;
    }

    public void setSecondaryMuscles(ArrayList<String> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg0() {
        return img0;
    }

    public void setImg0(String img0) {
        this.img0 = img0;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }
}
