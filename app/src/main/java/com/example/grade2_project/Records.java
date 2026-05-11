package com.example.grade2_project;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Previous_counters_values")

public class Records {
    @PrimaryKey(autoGenerate = true)

    public int id;
    public int qtdDown;
    public int qtdUp;
    public String data;

    public Records(int qtdDown, int qtdUp, String data) {
        this.qtdDown = qtdDown;
        this.qtdUp = qtdUp;
        this.data = data;
    }
}