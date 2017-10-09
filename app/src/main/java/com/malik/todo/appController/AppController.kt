package com.malik.todo.appController

import android.app.Application
import android.arch.persistence.room.Room
import com.malik.todo.R
import com.malik.todo.database.Migration1To2
import com.malik.todo.database.Migration2To3
import com.malik.todo.database.DatabaseManager

/**
 * Created by malik on 3/10/17.
 */
class AppController : Application() {

    companion object {
        var database: DatabaseManager? = null
        @JvmField
        val MIGRATION_1_2 = Migration1To2()
        val MIGRATION_2_3 = Migration2To3()
    }

    override fun onCreate() {
        super.onCreate()
        AppController.database = Room.databaseBuilder(this, DatabaseManager::class.java,
                getString(R.string.app_name) + ".db").addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }
}