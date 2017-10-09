package com.malik.todo.listeners

import android.view.View
import android.widget.AdapterView

/**
 * Created by malik on 5/10/17.
 */
class OnItemSelectedListener(val categoryName: CategoryName) : AdapterView.OnItemSelectedListener {


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val catName = parent!!.getItemAtPosition(pos).toString()
        categoryName.spinnerCatName(catName)
    }

    interface CategoryName {
        fun spinnerCatName(categoryName: String)
    }

}