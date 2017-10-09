package com.malik.todo.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Category")
data class Category(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
                    @ColumnInfo(name = "category_name") var name: String? = null,
                    @ColumnInfo(name = "type") var type: Int = 0)