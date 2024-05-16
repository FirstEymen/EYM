package com.bilireymen.eym.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Address
class CheckoutAddressItemAdapter(private val selectedAddress: Address) :
    RecyclerView.Adapter<CheckoutAddressItemAdapter.AddressViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkout_address_item, parent, false)
        return AddressViewHolder(view)
    }
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.addressNameTextView.text = selectedAddress.name ?: ""
        holder.addressTextView.text = selectedAddress.address ?: ""
    }
    override fun getItemCount(): Int {
        return 1
    }
    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressNameTextView = itemView.findViewById<TextView>(R.id.checkoutAddressName)
        val addressTextView = itemView.findViewById<TextView>(R.id.checkoutAddress)
    }
}