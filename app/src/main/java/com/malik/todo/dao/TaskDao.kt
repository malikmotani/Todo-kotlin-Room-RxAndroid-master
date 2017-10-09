package com.malik.todo.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import com.malik.todo.models.Task

import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable

@Dao
interface TaskDao {

    @Query("select * from Task")
    fun getAllTasks(): Flowable<List<Task>>

    @Query("select * from Task where type = :arg0")
    fun getAllTaskByType(type: Int): Flowable<List<Task>>

    @Query("select * from Task where id = :arg0")
    fun findTaskById(id: Long): Task

    @Insert(onConflict = REPLACE)
    fun insertTask(task: Task)

    @Update(onConflict = REPLACE)
    fun updateTask(task: Task) : Int

    @Delete
    fun deleteTask(task: Task)
}