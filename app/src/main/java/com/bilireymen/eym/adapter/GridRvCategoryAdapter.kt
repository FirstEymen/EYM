 package com.bilireymen.eym.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.CategoryProductsActivity
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.bumptech.glide.Glide

class GridRvCategoryAdapter(private val context: Context,
                            private var categoryArrayList: ArrayList<Category>,
                            private var onItemClickListener: GridRvCategoryAdapter.OnItemClickListener? = null):RecyclerView.Adapter<GridRvCategoryAdapter.GridCategoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridRvCategoryAdapter.GridCategoryHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_gridrvcategory_item,parent,false)
        return GridCategoryHolder(view)
    }

    override fun onBindViewHolder(holder: GridRvCategoryAdapter.GridCategoryHolder, position: Int) {
        holder.categoryItem = categoryArrayList[position]
        holder.nameTextView.text = holder.categoryItem.name
        Glide.with(holder.itemView.context).load(holder.categoryItem.images!!.get(0)).into(holder.categoryIv)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    inner class GridCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var categoryItem:Category
        var nameTextView: TextView = itemView.findViewById(R.id.gridRvCategoryName)
        var categoryIv: ImageView = itemView.findViewById(R.id.img_gridRvCategory)

        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position, categoryItem)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, category: Category)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

}