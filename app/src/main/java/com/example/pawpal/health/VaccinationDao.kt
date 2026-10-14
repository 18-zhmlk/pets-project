package com.example.pawpal.health

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {
    @Query("SELECT * FROM vaccinations ORDER BY id DESC")
    fun observeAll(): Flow<List<Vaccination>>

    @Insert
    suspend fun insert(item: Vaccination): Long

    @Update
    suspend fun update(item: Vaccination)

    @Delete
    suspend fun delete(item: Vaccination)
}
