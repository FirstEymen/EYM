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
import com.bilireymen.eym.CommonFunctions
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Product
import com.bumptech.glide.Glide

class GridRvAdapter(private val context: Context, private val productArrayList: ArrayList<Product>): RecyclerView.Adapter<GridRvAdapter.GridHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_gridrv_item, parent, false)
        return GridHolder(view)
    }

    override fun onBindViewHolder(holder: GridHolder, position: Int) {
        holder.productItem = productArrayList[position]
        holder.nameTextView.text = holder.productItem.name
        holder.priceTextView.text = "$" + holder.productItem.price.toString()
        holder.priceTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
        val oldPriceHtml = "<strike>${"$" + holder.productItem.offerPercentage}</strike>"
        holder.priceOldTextView.text = Html.fromHtml(oldPriceHtml)
        Glide.with(holder.itemView.context).load(holder.productItem.images!!.get(0)).into(holder.productIv)
        if (CommonFunctions.getFavoriteProductList().contains(holder.productItem.id))
           Glide.with(holder.favoriteIv.context).load(R.drawable.ic_favoritered).into(holder.favoriteIv)
        else
           Glide.with(holder.favoriteIv.context).load(R.drawable.ic_favorite).into(holder.favoriteIv)
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    inner class GridHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var productItem:Product
        var nameTextView: TextView = itemView.findViewById(R.id.gridRvName)
        var priceTextView: TextView = itemView.findViewById(R.id.gridRvPrice)
        var priceOldTextView: TextView = itemView.findViewById(R.id.gridRvOldPrice)
        var productIv: ImageView = itemView.findViewById(R.id.img_gridRv)
        var favoriteIv: ImageView = itemView.findViewById(R.id.img_favorite_grid)
        init {
            favoriteIv.setOnClickListener{
                CommonFunctions.setFavoriteProductList(productItem.id!!)
                this@GridRvAdapter.notifyDataSetChanged()
            }
        }
    }
}