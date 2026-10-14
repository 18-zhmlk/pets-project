package com.example.pawpal.health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccinations")
data class Vaccination(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String
)
