package com.malik.todo.framgments

import android.graphics.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.malik.todo.R
import com.malik.todo.adapters.TaskAdapter
import com.malik.todo.appController.AppController
import com.malik.todo.listeners.OnStartDragListener
import com.malik.todo.listeners.RecyclerItemClickListener
import com.malik.todo.models.Task
import com.malik.todo.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.util.ArrayList

/**
 * Created by malik on 6/10/17.
 */

class HistoryFragment : Fragment(), OnStartDragListener {

    val TAG: String = HistoryFragment::class.java.simpleName

    lateinit var txtNoHistory: TextView
    lateinit var recyclerViewHistory: RecyclerView

    var mArrayList: ArrayList<Task> = ArrayList()

    lateinit var taskAdapter: TaskAdapter

    lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_history, container, false)

        initialize(view)

        return view
    }

//    override fun onResume() {
//        super.onResume()
//        isTaskListEmpty()
//    }

    private fun initialize(view: View) {

        txtNoHistory = view.txtNoHistory
        recyclerViewHistory = view.recyclerViewHistory

        recyclerViewHistory.setHasFixedSize(true)
        recyclerViewHistory.layoutManager = LinearLayoutManager(activity!!)

        taskAdapter = TaskAdapter(activity, mArrayList)
        recyclerViewHistory.adapter = taskAdapter

        initSwipe()

        registerAllTakListener()

        recyclerViewHistory.addOnItemTouchListener(
                RecyclerItemClickListener(context, recyclerViewHistory, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Log.e(TAG, "item click Position : " + position)

                        val holder: TaskAdapter.ViewHolder = TaskAdapter.ViewHolder(view)

                        clickForDetails(holder, position)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        Log.e(TAG, "item long click Position : " + position)
                    }
                })
        )
    }

    private fun registerAllTakListener() {
        AppController.database?.taskDao()?.getAllTaskByType(DONE)
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ taskAdapter.tasks = it })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper.startDrag(viewHolder)
    }

    private fun clickForDetails(holder: TaskAdapter.ViewHolder, position: Int) {

        val taskList = taskAdapter.getList()

        if (holder.textTitle.visibility == View.GONE && holder.textTask.visibility == View.GONE) {

            holder.textTitle.visibility = View.VISIBLE
            holder.textTask.visibility = View.VISIBLE
            holder.txtShowTitle.maxLines = Integer.MAX_VALUE
            holder.txtShowTask.maxLines = Integer.MAX_VALUE

            if (taskList[position].date != "") {
                holder.txtShowDate.text = getFormatDate(taskList[position].date!!)
                holder.textDate.visibility = View.VISIBLE
                holder.txtShowDate.visibility = View.VISIBLE
            }

            if (taskList[position].time != "") {
                holder.txtShowTime.text = getFormatTime(taskList[position].time!!)
                holder.textTime.visibility = View.VISIBLE
                holder.txtShowTime.visibility = View.VISIBLE
            }

        } else {

            holder.textTitle.visibility = View.GONE
            holder.textTask.visibility = View.GONE
            holder.txtShowTask.maxLines = 1
            holder.txtShowTitle.maxLines = 1

            if (taskList[position].date != "") {
                holder.textDate.visibility = View.GONE
                holder.txtShowDate.visibility = View.GONE
            }

            if (taskList[position].time != "") {
                holder.textTime.visibility = View.GONE
                holder.txtShowTime.visibility = View.GONE
            }

        }
    }

    private fun initSwipe() {

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    taskAdapter.markTaskAs(position, DELETED)
                    isTaskListEmpty()
                } else {
                    taskAdapter.markTaskAs(position, ACTIVE)
                    isTaskListEmpty()
                }

            }

            override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                val itemView = viewHolder.itemView

                val paint = Paint()
                val iconBitmap: Bitmap

                if (dX > 0) {

                    iconBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_unfinish)

                    paint.color = Color.parseColor(getString(R.color.green))

                    canvas.drawRect(itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint)

                    // Set the image icon for Right side swipe
                    canvas.drawBitmap(iconBitmap,
                            itemView.left.toFloat() + convertDpToPx(16),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - iconBitmap.height.toFloat()) / 2,
                            paint)
                } else {

                    iconBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)

                    paint.color = Color.parseColor(getString(R.color.red))

                    canvas.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint)

                    //Set the image icon for Left side swipe
                    canvas.drawBitmap(iconBitmap,
                            itemView.right.toFloat() - convertDpToPx(16) - iconBitmap.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - iconBitmap.height.toFloat()) / 2,
                            paint)
                }

                val ALPHA_FULL = 1.0f

                // Fade out the view as it is swiped out of the parent's bounds
                val alpha: Float = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationX = dX

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewHistory)
    }


    private fun convertDpToPx(dp: Int): Int {
        return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun isTaskListEmpty() {
        if (taskAdapter.itemCount == 0) {
            txtNoHistory.visibility = View.VISIBLE
        } else {
            txtNoHistory.visibility = View.GONE
        }
    }
}