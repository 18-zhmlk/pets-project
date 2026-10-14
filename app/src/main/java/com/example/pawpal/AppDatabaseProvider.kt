package com.example.pawpal

import android.content.Context
import androidx.room.Room

object AppDatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pawpal_db"
            ).build()
        }
        return INSTANCE!!
    }
}

