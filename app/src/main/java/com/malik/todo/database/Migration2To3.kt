package com.malik.todo.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

/**
 * Created by malik on 6/10/17.
 */
class Migration2To3 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // perform alter
        database.execSQL("ALTER TABLE Category ADD COLUMN type INTEGER DEFAULT 0")

    }
}