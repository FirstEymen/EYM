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

class CheckoutItemAdapter(
    private val cartProducts: ArrayList<CartProduct>,
    private val subtotalPriceTextView: TextView
) : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutViewHolder>() {

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val cartProduct = cartProducts[position]

        Glide.with(holder.itemView.context).load(cartProduct.product.images!!.get(0))
            .into(holder.productImageImageView)
        holder.productNameTextView.text = cartProduct.product.name
        holder.productSizeTextView.text = cartProduct.selectedSize ?: ""
        holder.productPriceTextView.text = "$${cartProduct.product.price}"

        val subtotalPrice = calculateSubtotal(cartProducts)
        subtotalPriceTextView.text = "$${subtotalPrice}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkout_item, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageImageView: ImageView = itemView.findViewById(R.id.checkoutImage)
        val productNameTextView = itemView.findViewById<TextView>(R.id.checkoutImageName)
        val productSizeTextView = itemView.findViewById<TextView>(R.id.checkoutImageSize)
        val productPriceTextView = itemView.findViewById<TextView>(R.id.checkoutImagePrice)
    }

    private fun calculateSubtotal(cartProducts: List<CartProduct>): Double {
        var subtotal = 0.0
        for (cartProduct in cartProducts) {
            subtotal += cartProduct.product.price!!
        }
        return subtotal
    }
}