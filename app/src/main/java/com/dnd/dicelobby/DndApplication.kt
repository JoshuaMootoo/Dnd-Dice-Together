package com.dnd.dicelobby

import android.app.Application
import com.dnd.dicelobby.database.AppDatabase

/**
 * Application class — initialises the Room database singleton used throughout the app.
 */
class DndApplication : Application() {

    /** Lazily-created Room database instance shared across the whole app. */
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
