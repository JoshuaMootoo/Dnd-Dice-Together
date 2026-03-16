package com.dnd.dicelobby.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Single Room database for the application.
 * Currently stores only the roll history; can be extended with player preferences etc.
 */
@Database(entities = [RollEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rollDao(): RollDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dnd_dice_lobby.db"
                ).build().also { INSTANCE = it }
            }
    }
}
