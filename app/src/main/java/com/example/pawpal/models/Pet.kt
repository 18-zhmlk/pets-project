package com.example.pawpal.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность питомца.
 * Поля подобраны под твой экран (включил поля владельца, чтобы всё было в одном месте).
 * Можешь убрать/добавить поля при необходимости.
 */
@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // --- Данные питомца ---
    val name: String,        // Имя: Пушистик
    val type: String,        // Тип: Кот
    val breed: String,       // Порода: Шотландская
    val gender: String,      // Пол: Женский
    val birthdate: String,   // Дата рождения: 28/04/2021 (храним строкой для простоты)

    // --- Данные владельца (необязательные, но полезны для твоей карточки) ---
    val ownerName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = "",
    var avatarUri: String? = null
)
