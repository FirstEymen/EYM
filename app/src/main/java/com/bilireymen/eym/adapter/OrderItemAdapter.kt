package com.bilireymen.eym.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.models.CartProduct
import com.bumptech.glide.Glide
class OrderItemAdapter(private val cartProducts: ArrayList<CartProduct>):RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>(){
    override fun onBindViewHolder(holder: OrderItemAdapter.OrderViewHolder, position: Int) {
        val cartProduct = cartProducts[position]
        Glide.with(holder.itemView.context).load(cartProduct.product.images!!.get(0))
            .into(holder.productImageImageView)
        holder.productNameTextView.text = cartProduct.product.name
        holder.productSizeTextView.text = cartProduct.selectedSize ?: ""
        holder.productPriceTextView.text = "$${cartProduct.product.price}"
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemAdapter.OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }
    override fun getItemCount(): Int {
        return cartProducts.size
    }
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageImageView: ImageView = itemView.findViewById(R.id.orderImage)
        val productNameTextView = itemView.findViewById<TextView>(R.id.orderImageName)
        val productSizeTextView = itemView.findViewById<TextView>(R.id.orderImageSize)
        val productPriceTextView = itemView.findViewById<TextView>(R.id.orderImagePrice)
    }
    private fun calculateSubtotal(cartProducts: List<CartProduct>): Double {
        var subtotal = 0.0
        for (cartProduct in cartProducts) {
            subtotal += cartProduct.product.price!!
        }
        return subtotal
    }
}