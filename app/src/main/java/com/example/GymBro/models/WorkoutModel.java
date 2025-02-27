package com.example.GymBro.models;

public class WorkoutModel {


    private int dayNumber;

    public WorkoutModel() { }

    public WorkoutModel(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayNumber % 7];
    }
}

