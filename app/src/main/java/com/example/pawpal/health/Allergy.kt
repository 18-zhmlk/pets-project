package com.example.pawpal.health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allergies")
data class Allergy(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val note: String
)
