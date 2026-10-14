package com.example.pawpal.dao

import androidx.room.*
import com.example.pawpal.models.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    // Получить всех питомцев (как список)
    @Query("SELECT * FROM pets ORDER BY id ASC")
    suspend fun getAll(): List<Pet>

    // Реактивный поток (если нужно наблюдать изменения)
    @Query("SELECT * FROM pets ORDER BY id ASC")
    fun observeAll(): Flow<List<Pet>>

    // Получить одного по id
    @Query("SELECT * FROM pets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Pet?

    // Вставка
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pet: Pet): Long

    // Обновление
    @Update
    suspend fun update(pet: Pet)

    // Удаление
    @Delete
    suspend fun delete(pet: Pet)

    // Удобный апсерт
    @Transaction
    suspend fun upsert(pet: Pet): Long {
        val id = insert(pet)
        // Если insert заменил существующую запись (REPLACE), Room вернёт rowId новой записи.
        // Для стабильности можно просто вернуть id.
        return id
    }
}

