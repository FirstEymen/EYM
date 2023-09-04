package com.bilireymen.eym.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.CommonFunctions
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Product
import com.bumptech.glide.Glide

class HorizontalRvItemAdapter(private val context: Context, private val productArrayList: ArrayList<Product>,private var onItemClickListener: OnItemClickListener? = null): RecyclerView.Adapter<HorizontalRvItemAdapter.HorizontalHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_horizontalrv_item, parent, false)
        return HorizontalHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalHolder, position: Int) {
            holder.productItem = productArrayList[position]

            holder.nameTextView.text =  holder.productItem?.name
            holder.priceTextView.text = "$" + holder.productItem?.price.toString()
            Glide.with(holder.itemView.context).load(productArrayList.get(position).images!!.get(0)).into(holder.productIv)
        if (CommonFunctions.getFavoriteProductList().contains(holder.productItem?.id))
            Glide.with(holder.favoriteIv.context).load(R.drawable.ic_favoritered).into(holder.favoriteIv)
        else
            Glide.with(holder.favoriteIv.context).load(R.drawable.ic_favorite).into(holder.favoriteIv)
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    inner class HorizontalHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var productItem:Product?=null
        var nameTextView: TextView = itemView.findViewById(R.id.horizontalName)
        var priceTextView: TextView = itemView.findViewById(R.id.horizontalPrice)
        var productIv: ImageView = itemView.findViewById(R.id.imgHorizontal)
        var favoriteIv: ImageView = itemView.findViewById(R.id.img_favorite_horizontal)
        init {
            favoriteIv.setOnClickListener{
                CommonFunctions.setFavoriteProductList(productItem?.id!!)
                this@HorizontalRvItemAdapter.notifyDataSetChanged()
            }
        }

        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position, productItem!!)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, product: Product)

    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}