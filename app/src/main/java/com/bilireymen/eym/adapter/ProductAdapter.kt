package com.bilireymen.eym.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.AdminMproductListActivity
import com.bilireymen.eym.AdminProductAdd
import com.bilireymen.eym.databinding.ActivityAdminMproductListRowBinding
import com.bilireymen.eym.models.Product
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProductAdapter(private val productArrayList: ArrayList<Product>,private var context:Context) :
    RecyclerView.Adapter<ProductAdapter.ProductHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val activityAdminMproductListRowBinding = ActivityAdminMproductListRowBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        return ProductHolder(activityAdminMproductListRowBinding)
    }

    override fun getItemCount(): Int {return productArrayList.size}


    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.product=productArrayList.get(position)
        holder.activityAdminMproductListRowBinding.productListName.text = productArrayList[position].name
        holder.activityAdminMproductListRowBinding.productListPrice.text = productArrayList[position].price.toString()
        holder.activityAdminMproductListRowBinding.productListOfferPercentage.text = productArrayList[position].offerPercentage.toString()
       Glide.with(holder.itemView.context).load(productArrayList.get(position).images!!.get(0)).into(holder.activityAdminMproductListRowBinding.productListImage)
    }


    inner class ProductHolder(activityAdminMproductListRowBinding:ActivityAdminMproductListRowBinding) : RecyclerView.ViewHolder(activityAdminMproductListRowBinding.root),OnClickListener{
           var product: Product? =null
         var activityAdminMproductListRowBinding: ActivityAdminMproductListRowBinding=activityAdminMproductListRowBinding
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val intent=Intent(context,AdminProductAdd::class.java)
            intent.putExtra("product",product)
            context.startActivity(intent)
        }

    }





}