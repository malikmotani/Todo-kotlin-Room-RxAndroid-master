package com.malik.todo.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.malik.todo.dao.CategoryDao
import com.malik.todo.dao.TaskDao
import com.malik.todo.models.Category
import com.malik.todo.models.Task

@Database(entities = arrayOf(Category::class, Task::class), version = 3)
abstract class DatabaseManager : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
}