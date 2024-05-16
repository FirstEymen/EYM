package com.bilireymen.eym.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Product
import com.bumptech.glide.Glide
class CarouselRvAdapter(private val context: Context,
                        private var productArrayList: ArrayList<Product>,
                        private var onItemClickListener: OnItemClickListener? = null): RecyclerView.Adapter<CarouselRvAdapter.CarouselHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselRvAdapter.CarouselHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_carouselrv_item, parent, false)
        return CarouselHolder(view)
    }
    override fun onBindViewHolder(holder: CarouselRvAdapter.CarouselHolder, position: Int) {
        holder.productItem = productArrayList[position]
        holder.nameTextView.text = holder.productItem.name
        holder.priceTextView.text ="$" + holder.productItem.price.toString()
        holder.priceTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
        val oldPriceHtml = "<strike>${"$" + holder.productItem.offerPercentage}</strike>"
        holder.priceOldTextView.text = Html.fromHtml(oldPriceHtml)
        Glide.with(holder.itemView.context).load(holder.productItem.images!!.get(0)).into(holder.productIv)
    }
    override fun getItemCount(): Int {
        return productArrayList.size
    }
    inner class CarouselHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var productItem:Product
        var nameTextView: TextView = itemView.findViewById(R.id.carouselRvName)
        var priceTextView: TextView = itemView.findViewById(R.id.carouselRvNewPrice)
        var priceOldTextView: TextView = itemView.findViewById(R.id.carouselRvOldPrice)
        var productIv: ImageView = itemView.findViewById(R.id.img_carouselRv_item)
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position, productItem)
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