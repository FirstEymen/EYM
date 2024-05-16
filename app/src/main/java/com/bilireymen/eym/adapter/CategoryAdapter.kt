package com.bilireymen.eym.adapter

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.AdminCategoryAddActivity
import com.bilireymen.eym.AdminCategoryListActivity
import com.bilireymen.eym.databinding.ActivityAdminCategoryListRowBinding
import com.bilireymen.eym.models.Category
import com.bumptech.glide.Glide
class CategoryAdapter(private val categoryArrayList: ArrayList<Category>, private var context: Context,private var from:String?):
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val activityAdminCategoryListRowBinding = ActivityAdminCategoryListRowBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
            return CategoryHolder(activityAdminCategoryListRowBinding)
    }
    override fun getItemCount(): Int {return categoryArrayList.size}
    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.category=categoryArrayList.get(position)
        holder.activityAdminCategoryListRowBinding.categoryListName.text = categoryArrayList[position].name
        Glide.with(holder.itemView.context).load(categoryArrayList.get(position).images!!.get(0)).into(holder.activityAdminCategoryListRowBinding.categoryListImage)
    }
    inner class CategoryHolder(activityAdminCategoryListRowBinding: ActivityAdminCategoryListRowBinding):RecyclerView.ViewHolder(activityAdminCategoryListRowBinding.root),OnClickListener{
        var category: Category? = null
        var activityAdminCategoryListRowBinding: ActivityAdminCategoryListRowBinding =
            activityAdminCategoryListRowBinding
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            if (from != null && from.equals("product")) {
                val intent:Intent=Intent()
                intent.putExtra("category",category)
                (context as AdminCategoryListActivity).setResult(RESULT_OK,intent)
                (context as AdminCategoryListActivity).finish()
            } else {
                val intent = Intent(context, AdminCategoryAddActivity::class.java)
                intent.putExtra("category", category)
                context.startActivity(intent)
            }
        }
    }
    }