package com.malik.todo.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

/**
 * Created by malik on 6/10/17.
 */
class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // perform alter
        database.execSQL("ALTER TABLE Task ADD COLUMN type INTEGER DEFAULT 0 ")

    }
}