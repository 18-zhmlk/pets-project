package com.example.pawpal.health

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergyDao {
    @Query("SELECT * FROM allergies ORDER BY id DESC")
    fun observeAll(): Flow<List<Allergy>>

    @Insert
    suspend fun insert(item: Allergy): Long

    @Update
    suspend fun update(item: Allergy)

    @Delete
    suspend fun delete(item: Allergy)
}
