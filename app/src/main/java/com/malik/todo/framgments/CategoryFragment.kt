package com.malik.todo.framgments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.malik.todo.R
import com.malik.todo.adapters.CategoryAdapter
import com.malik.todo.appController.AppController
import com.malik.todo.models.Category
import com.malik.todo.utils.ACTIVE
import com.malik.todo.utils.dialogAddCategory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category.view.*
import java.util.*

/**
 * Created by malik on 6/10/17.
 */

class CategoryFragment : Fragment(), View.OnClickListener {

    val TAG: String = CategoryFragment::class.java.simpleName

    lateinit var fabAddCategory: FloatingActionButton
    lateinit var recyclerViewCategory: RecyclerView
    lateinit var txtNoCategory: TextView

    var mArrayList: ArrayList<Category> = ArrayList()
    lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_category, container, false)

        initialize(view)

        return view
    }

    /**
     * initializing views and data
     * */
    private fun initialize(view: View) {

        fabAddCategory = view.fabAddCategory
        recyclerViewCategory = view.recyclerViewCategory
        txtNoCategory = view.txtNoCategory

        recyclerViewCategory.setHasFixedSize(true)
        recyclerViewCategory.layoutManager = LinearLayoutManager(activity!!)

        fabAddCategory.setOnClickListener(this)

        categoryAdapter = CategoryAdapter(activity, mArrayList)
        recyclerViewCategory.adapter = categoryAdapter

        registerCategoryListener()
    }

    private fun registerCategoryListener() {
        AppController.database?.categoryDao()?.getAllCategoryByType(ACTIVE)
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ categoryAdapter.categories = it })
    }

    /**
     * Views clicks
     * */
    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.fabAddCategory -> {
                dialogAddCategory(activity)
            }
        }
    }
}