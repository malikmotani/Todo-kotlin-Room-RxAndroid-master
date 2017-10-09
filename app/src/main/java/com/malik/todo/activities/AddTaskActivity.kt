package com.malik.todo.activities

import android.app.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.malik.todo.R
import com.malik.todo.appController.AppController
import com.malik.todo.listeners.OnItemSelectedListener
import com.malik.todo.models.Category
import com.malik.todo.models.Task
import com.malik.todo.utils.ACTIVE
import com.malik.todo.utils.dialogAddCategory
import com.malik.todo.utils.hideKeyboard
import com.malik.todo.utils.toastMessage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_task.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by malik on 5/10/17.
 */

class AddTaskActivity : AppCompatActivity(), View.OnClickListener, OnItemSelectedListener.CategoryName {

    val TAG: String = MainActivity::class.java.simpleName

    val mActivity: Activity = this@AddTaskActivity

    lateinit var myCalendar: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    //Final variable to save in database
    private var finalDate = ""
    private var finalTime = ""
    private var finalTitle = ""
    private var finalTask = ""
    private var finalCategoryName = ""
    private var finalCategoryId = 0
    private lateinit var listCategory: List<Category>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        initialize()
    }


    /**
     * initializing views and data
     * */
    private fun initialize() {
        toolbarAddTask.title = getString(R.string.add_task)
        setSupportActionBar(toolbarAddTask)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        /**
         * click listener
         * */
        edtSetDate.setOnClickListener(this)
        edtSetTime.setOnClickListener(this)
        imgCancelDate.setOnClickListener(this)
        imgCancelTime.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)

        /**
         * load category in spinner
         * */
        readSpinner()
    }

    /**
     * action bar back button click
     * */
    override fun onSupportNavigateUp(): Boolean {
        checkTask()
        return super.onSupportNavigateUp()
    }

    /**
     * inflating actionbar menu
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * actionbar clicks
     * */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId

        when (id) {
            R.id.action_done -> {
                addTask()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        checkTask()
    }

    /**
     * on leaving this screen this method will check
     * weather user have enter any task or not If YES
     * it will show dialog else finish()
     * */
    private fun checkTask() {

        finalTitle = edtTitle.text.toString().trim()
        finalTask = edtTask.text.toString().trim()

        if (finalTitle != "" && finalTask != "") {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mActivity)

            alertDialog.setTitle(getString(R.string.save_task))
            alertDialog.setMessage(getString(R.string.do_you_want_to_save_this_task))

            alertDialog.setPositiveButton(R.string.save, { _, _ ->
                addTask()
            })

            alertDialog.setNegativeButton(R.string.cancel, { _, _ ->
                finish()
            })

            val alert: AlertDialog = alertDialog.create()
            alert.show()
        } else {
            finish()
        }

    }

    /**
     * Add Task in database
     * */
    private fun addTask() {

        finalTitle = edtTitle.text.toString().trim()
        finalTask = edtTask.text.toString().trim()
        finalTime = edtSetTime.text.toString()
        finalDate = edtSetDate.text.toString()
        var task = Task()
//
        if (finalTitle.isEmpty()) {
            toastMessage(this, getString(R.string.please_add_title))
            return
        } else if (finalTask.isEmpty()) {
            toastMessage(mActivity, getString(R.string.please_add_task))
            return
        } else if (listCategory.isEmpty()) {
            toastMessage(mActivity, getString(R.string.please_add_category))
            return
        } else {
            task.category = listCategory[spinnerCategory.selectedItemPosition]
            task.title = finalTitle
            task.description = finalTask
            task.time = finalTime
            task.date = finalDate
            task.type = ACTIVE

            Log.d(TAG, "Title : " + finalTitle + "\nTask : " + finalTask +
                    "\nDate : " + finalDate + "\nTime : " + finalTime +
                    "\nCategory : " + finalCategoryName)

            Single.fromCallable { AppController.database?.taskDao()?.insertTask(task) }
                    .subscribeOn(Schedulers.newThread())
                    .subscribe()

            finish()
        }
    }

    private fun readSpinner() {
        AppController.database?.categoryDao()?.getAllCategoryByType(ACTIVE)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { listCategory ->
                    loadDataInSpinner(listCategory)
                }
    }

    /**
     * load category in spinner
     * */
    private fun loadDataInSpinner(listCategory: List<Category>) {

        this.listCategory = listCategory
        var labels: ArrayList<String>? = ArrayList()
        for (category in listCategory) {
            labels?.add(category.name!!)
        }
        if (labels!!.isEmpty()) {
            val arrayList: ArrayList<String> = ArrayList()
            arrayList.add(getString(R.string.no_category_found))
            labels = arrayList
        }

        val dataAdapter = ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, labels)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        Collections.sort(labels)

        spinnerCategory.adapter = dataAdapter

        spinnerCategory.onItemSelectedListener = OnItemSelectedListener(this)
    }

    override fun spinnerCatName(categoryName: String) {
        if (categoryName != getString(R.string.no_category__added)) {
            if (categoryName != "") {
                finalCategoryName = categoryName
            }
        }

    }

    /**
     * Views clicks
     * */
    override fun onClick(view: View?) {
        view?.hideKeyboard()

        when (view!!.id) {
            R.id.edtSetDate -> {
                dateAndTime()
                setDate()
            }
            R.id.edtSetTime -> {
                dateAndTime()
                setTime()
            }
            R.id.imgCancelDate -> {
                edtSetDate.setText("")
                finalDate = ""
                imgCancelDate.visibility = View.GONE
                if (relativeLayoutTime.visibility == View.VISIBLE) {
                    relativeLayoutTime.visibility = View.GONE
                    edtSetTime.setText("")
                    finalTime = ""
                    imgCancelTime.visibility = View.GONE
                }

            }
            R.id.imgCancelTime -> {
                edtSetTime.setText("")
                finalTime = ""
                imgCancelTime.visibility = View.GONE
            }
            R.id.imgAddCategory -> {
                dialogAddCategory(mActivity)
            }
        }
    }

    /**
     * current Date and Time initialize
     * */
    private fun dateAndTime() {

        myCalendar = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabelDate()
        }

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, minute)
            updateLabelTime()
        }

    }

    /**
     * @DatePickerDialog for selecting date
     * */
    private fun setDate() {

        val datePickerDialog = DatePickerDialog(this, dateSetListener, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()

    }


    /**
     * @TimePickerDialog for selecting time
     * */
    private fun setTime() {
        val timePickerDialog = TimePickerDialog(this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }

    /**
     * UI Update of time
     * */
    private fun updateLabelTime() {

        val myFormat = "HH:mm"  // HH:mm:ss
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        finalTime = sdf.format(myCalendar.time)


        val myFormat2 = "h:mm a"
        val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
        edtSetTime.setText(sdf2.format(myCalendar.time))

        imgCancelTime.visibility = View.VISIBLE
    }


    /**
     * UI Update of time
     * */
    private fun updateLabelDate() {

        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        finalDate = sdf.format(myCalendar.time)


        val myFormat2 = "EEE, d MMM yyyy"
        val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
        edtSetDate.setText(sdf2.format(myCalendar.time))

        relativeLayoutTime.visibility = View.VISIBLE
        imgCancelDate.visibility = View.VISIBLE
    }

}
