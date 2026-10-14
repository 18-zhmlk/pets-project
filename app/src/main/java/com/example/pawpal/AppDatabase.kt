package com.example.pawpal

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pawpal.dao.PetDao
import com.example.pawpal.models.Pet
import com.example.pawpal.health.*

@Database(
    entities = [Pet::class, Vaccination::class, Allergy::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun allergyDao(): AllergyDao
}
