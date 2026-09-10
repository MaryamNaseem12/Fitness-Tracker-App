package com.example.fitnesstracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_goals")
public class DailyGoal {

    @PrimaryKey
    private int id = 1;

    private int stepGoal;
    private int calorieGoal;
    private int waterGoalMl;
    private int activeMinutesGoal;

    public DailyGoal(int stepGoal, int calorieGoal, int waterGoalMl, int activeMinutesGoal) {
        this.id = 1;
        this.stepGoal = stepGoal;
        this.calorieGoal = calorieGoal;
        this.waterGoalMl = waterGoalMl;
        this.activeMinutesGoal = activeMinutesGoal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public int getWaterGoalMl() {
        return waterGoalMl;
    }

    public void setWaterGoalMl(int waterGoalMl) {
        this.waterGoalMl = waterGoalMl;
    }

    public int getActiveMinutesGoal() {
        return activeMinutesGoal;
    }

    public void setActiveMinutesGoal(int activeMinutesGoal) {
        this.activeMinutesGoal = activeMinutesGoal;
    }
}
