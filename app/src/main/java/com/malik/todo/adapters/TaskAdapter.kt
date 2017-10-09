package com.malik.todo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.malik.todo.R
import com.malik.todo.appController.AppController
import com.malik.todo.models.Task
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.row_task.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by malik on 4/10/17.
 */
class TaskAdapter(private val mContext: Context, mArrayList: ArrayList<Task>) :
        RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    var isInit = false

    var pos = 0;
    var tasks: List<Task> = mArrayList
        set(value) {
            field = value
            if (isInit) {
                if (tasks.isNotEmpty())
                    notifyItemRangeChanged(pos, tasks.size)
                else
                    notifyDataSetChanged()
            } else {
                isInit = true
                notifyDataSetChanged()
            }
        }


    val TAG: String = TaskAdapter::class.java.simpleName

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.row_task, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        val androidColors = mContext.resources.getIntArray(R.array.random_color)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        holder!!.viewColorTag.setBackgroundColor(randomAndroidColor)

//        var currentTask: Task = mArrayList[position]
        holder.txtShowTitle.text = tasks[position].title
        holder.txtShowTask.text = tasks[position].description
        holder.txtShowCategory.text = tasks[position].category?.name.toString()

    }

    fun getList(): List<Task> {
        return tasks
    }

    fun deleteTask(position: Int) {

        pos = position
        Single.fromCallable {
            AppController.database?.taskDao()?.deleteTask(tasks[position])
        }.subscribeOn(Schedulers.io())
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ _ ->
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, tasks.size)
                })
    }

    fun markTaskAs(position: Int, type: Int) {

        pos = position

        tasks[position].type = type


        Single.fromCallable {
            AppController.database?.taskDao()?.updateTask(tasks[position])

        }.subscribeOn(Schedulers.io())
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ _ ->
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, tasks.size)
                })
    }

    fun updateTask(task: Task) {
        Single.fromCallable {
            AppController.database?.taskDao()?.updateTask(task)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

//    fun finishTask(position: Int, task: Task) {
//        Single.fromCallable {
//            AppController.database?.taskDao()?.updateTask(task)
//        }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe()
//
//        mArrayList.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, mArrayList.size)
//    }
//
//    fun unFinishTask(position: Int) {
//        dbManager.unFinishTask(mArrayList[position].id!!)
//        mArrayList.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, mArrayList.size)
//    }

//    override fun onItemDismiss(position: Int) {
////        tasks.removeAt(position)
//        notifyItemRemoved(position)
//    }
//
//    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
//        Collections.swap(tasks, fromPosition, toPosition)
//        notifyItemMoved(fromPosition, toPosition)
//        return true
//    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColorTag = view.viewColorTag!!
        val txtShowTitle = view.txtShowTitle!!
        val txtShowTask = view.txtShowTask!!
        val txtShowCategory = view.txtShowCategory!!

        val txtShowDate = view.txtShowDate!!
        val textDate = view.textDate!!
        val txtShowTime = view.txtShowTime!!
        val textTime = view.textTime!!
        val textTitle = view.textTitle!!
        val textTask = view.textTask!!
    }
}