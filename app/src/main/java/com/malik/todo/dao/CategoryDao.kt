package com.malik.todo.dao

import android.arch.persistence.room.*
import com.malik.todo.models.Category
import com.malik.todo.models.Task
import io.reactivex.Flowable

@Dao
interface CategoryDao {

    @Query("select * from Category where type = :arg0")
    fun getAllCategoryByType(type: Int): Flowable<List<Category>>

    @Query("select * from Category")
    fun getAllCategory(): Flowable<List<Category>>

    @Query("select * from Category where id = :arg0")
    fun findTaskById(id: Long): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)
}