package com.example.grade2_project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecordsDao {
    @Insert
    void insert(Records records);

    @Query("SELECT * FROM Previous_counters_values ORDER BY id DESC")
    List<Records> buscarTodos();
}
