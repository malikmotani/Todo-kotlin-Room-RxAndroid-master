package com.malik.todo.utils

import android.content.ContentValues
import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.malik.todo.R
import com.malik.todo.appController.AppController
import com.malik.todo.models.Category
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import io.reactivex.Single


/**
 * Created by malik on 4/10/17.
 */
/**
 * Toast message
 * */
fun toastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * Convert formatted Date
 * */
fun getFormatDate(inputDate: String): String {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputFormat = SimpleDateFormat("EEE, d MMM yyyy")

    var date: Date? = null
    try {
        date = inputFormat.parse(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val outputDate = outputFormat.format(date)
    return outputDate
}

/**
 * Convert formatted Time
 * */
fun getFormatTime(inputTime: String): String {

    val inputFormat = SimpleDateFormat("HH:mm") // HH:mm:ss
    val outputFormat = SimpleDateFormat("h:mm a")

    var date: Date? = null
    try {
        date = inputFormat.parse(inputTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val outputTime = outputFormat.format(date)
    return outputTime
}

/**
 * dialog to add category
 * */
fun dialogAddCategory(context: Context) {

    val li = LayoutInflater.from(context)
    val promptsView = li.inflate(R.layout.alert_dialog_add_category, null)

    val alert = AlertDialog.Builder(context)
    alert.setView(promptsView)

    val input: EditText = promptsView.findViewById(R.id.edtAddCat) as EditText

    alert.setPositiveButton(R.string.add, { _, _ -> })

    alert.setNegativeButton(R.string.cancel, { _, _ -> })
    val alertDialog = alert.create()

    alertDialog.setOnShowListener({

        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setOnClickListener({

            val categoryName: String = input.text.toString().trim()

            Log.i(ContentValues.TAG, "Category : " + categoryName)

            if (!categoryName.isEmpty()) {

                var category = Category()
                category.name = categoryName
                category.type = ACTIVE

                Single.fromCallable {
                    AppController.database?.categoryDao()?.insertCategory(category)
                }.subscribeOn(Schedulers.newThread())
                        .subscribe()

                alertDialog.dismiss()
            } else {
                toastMessage(context, context.getString(R.string.please_enter_category_to_add))
            }
        })
    })

    alertDialog.show()
}

/**
 * dialog to update category
 * */

fun dialogUpdateCategory(context: Context,category: Category) {


    val li = LayoutInflater.from(context)
    val promptsView = li.inflate(R.layout.alert_dialog_update_category, null)

    val alert = AlertDialog.Builder(context)
    alert.setView(promptsView)

    val input: EditText = promptsView.findViewById(R.id.edtUpdateCat) as EditText

    input.setText(category.name)
    input.setSelection(input.text.length)

    alert.setPositiveButton(R.string.update, { _, _ -> })

    alert.setNegativeButton(R.string.cancel, { _, _ -> })
    val alertDialog = alert.create()


    alertDialog.setOnShowListener({

        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setOnClickListener({

            val cat: String = input.text.toString().trim()

            Log.e(ContentValues.TAG, "Category : " + cat)

            if (cat != "") {
                if (cat != category.name) {

                    category.name = cat
                    Single.fromCallable {
                        AppController.database?.categoryDao()?.updateCategory(category)
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe()

                    alertDialog.dismiss()
                } else {
                    toastMessage(context, context.getString(R.string.please_edit_category_to_update))
                }
            } else {
                toastMessage(context, context.getString(R.string.please_enter_something_to_update))
            }
        })
    })

    alertDialog.show()
}


/**
 * dialog to delete category
 * */

fun dialogDeleteCategory(context: Context, category: Category) {


    val alert = AlertDialog.Builder(context)
    alert.setTitle(context.getString(R.string.delete_category))
    alert.setMessage(context.getString(R.string.delete_category_confimation))

    alert.setPositiveButton(R.string.delete, { _, _ -> })

    alert.setNegativeButton(R.string.cancel, { _, _ -> })

    val alertDialog = alert.create()

    alertDialog.setOnShowListener({

        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setOnClickListener({

            category.type = DELETED
            Single.fromCallable {
                AppController.database?.categoryDao()?.updateCategory(category)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()


//            val mArrayList = dbManager.getCategoryList()
//            categoryDeleted.isCategoryDeleted(true, mArrayList)

            alertDialog.dismiss()
        })
    })

    alertDialog.show()
}


/**
 * hide keyboard
 *
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}