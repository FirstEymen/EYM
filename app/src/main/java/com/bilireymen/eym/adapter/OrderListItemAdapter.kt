package com.bilireymen.eym.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.adapter.OrderListItemAdapter.*
import com.bilireymen.eym.models.Order
class OrderListItemAdapter(var orderList:ArrayList<Order>,var context: Context):RecyclerView.Adapter<OrderListViewHolder>(){
    inner class OrderListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val orderAddress = itemView.findViewById<TextView>(R.id.orderAddress)
        val cartProductRv=itemView.findViewById<RecyclerView>(R.id.cartProductRv)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_list_item, parent, false)
        return OrderListViewHolder(view)
    }
    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.orderAddress.text=orderList.get(position).checkout!!.address!!.address
        holder.cartProductRv.layoutManager=LinearLayoutManager(context)
        val orderProductAdapter = OrderItemAdapter(orderList.get(position).checkout!!.cartProduct!!)
        holder.cartProductRv.adapter=orderProductAdapter
    }
    override fun getItemCount(): Int {
        return orderList.size
    }
}