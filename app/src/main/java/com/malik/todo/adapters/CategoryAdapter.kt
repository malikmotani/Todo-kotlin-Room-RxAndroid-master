package com.malik.todo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.malik.todo.R
import com.malik.todo.models.Category
import com.malik.todo.utils.dialogDeleteCategory
import com.malik.todo.utils.dialogUpdateCategory
import kotlinx.android.synthetic.main.row_category.view.*

/**
 * Created by malik on 6/10/17.
 */

class CategoryAdapter(val mContext: Context, mArrayList: ArrayList<Category>) :
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    var categories: List<Category> = mArrayList
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    val TAG = CategoryAdapter::class.java.simpleName!!

    var mArrayList: ArrayList<Category> = ArrayList()

    var inflater: LayoutInflater

    init {
        this.mArrayList = mArrayList
        inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * Binding views with Adapter
     * */
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.txtCategoryName.text = categories[position].name
        holder.imgEditCategory.setOnClickListener({
            dialogUpdateCategory(mContext, categories[position])
        })
        holder.imgDeleteCategory.setOnClickListener({
            dialogDeleteCategory(mContext, categories[position])
        })
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    /**
     * Inflating layout
     * */
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.row_category, parent, false)
        return ViewHolder(mView)
    }

    /**
     * @ViewHolder class
     * initialize view
     * */
    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
        val txtCategoryName: TextView = view!!.txtCategoryName
        val imgEditCategory: ImageView = view!!.imgEditCategory
        val imgDeleteCategory: ImageView = view!!.imgDeleteCategory
    }
}