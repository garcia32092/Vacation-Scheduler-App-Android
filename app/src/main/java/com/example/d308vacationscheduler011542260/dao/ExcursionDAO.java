package com.example.d308vacationscheduler011542260.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacationscheduler011542260.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    // gets all excursions
    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionID ASC")
    List<Excursion> getAllExcursions();

    // gets all excursions that are associated with a vacation
    @Query("SELECT * FROM EXCURSIONS WHERE vacationID=:vacation ORDER BY excursionID ASC")
    List<Excursion> getAssociatedExcursions(int vacation);
}
