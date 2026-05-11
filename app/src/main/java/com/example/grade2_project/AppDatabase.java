package com.example.grade2_project;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Records.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract RecordsDao recordsDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "counter_db").build();
        }
        return instance;
    }
}
