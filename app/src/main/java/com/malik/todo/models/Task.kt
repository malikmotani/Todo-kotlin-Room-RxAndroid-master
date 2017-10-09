package com.malik.todo.models

import android.arch.persistence.room.*

/**
 * Created by malik on 3/10/17.
 */
@Entity(foreignKeys = arrayOf(ForeignKey(entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("task_categoryid"),
        onDelete = ForeignKey.CASCADE)))

data class Task(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
                @ColumnInfo(name = "title") var title: String? = null,
                @ColumnInfo(name = "description") var description: String? = null,
                @ColumnInfo(name = "date") var date: String? = null,
                @ColumnInfo(name = "time") var time: String? = null,
                @Embedded(prefix = "task_category") var category: Category? = null,
                @ColumnInfo(name = "type") var type: Int = 0)