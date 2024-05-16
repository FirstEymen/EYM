package com.bilireymen.eym

import android.content.Intent
import android.media.browse.MediaBrowser.ItemCallback
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.adapter.ProductAdapter
import com.bilireymen.eym.databinding.ActivityAdminMproductListBinding
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
class AdminMproductListActivity:AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityAdminMproductListBinding
    lateinit var productArrayList: ArrayList<Product>
    lateinit var productAdapter: ProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMproductListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        productArrayList = ArrayList()
        val uuid = UUID.randomUUID()
        val imageName = "images/$uuid.jpg"
        firestore=FirebaseFirestore.getInstance()
        binding.productListRecyclerView.layoutManager = GridLayoutManager(this,2)
        productAdapter = ProductAdapter(productArrayList,this)
        binding.productListRecyclerView.adapter = productAdapter
        getData()
    }
    private fun getData() {
        firestore.collection("Products")
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    val data: Map<String, Any> = document.data
                    val id = data["id"] as String?
                    val name = data["name"] as String
                    val price = data["price"] as Double
                    val offerPercentage = data["offerPercentage"] as Double
                    val description = data["description"] as String
                    val sizes = data["sizes"] as List<String>
                    val images = data["images"] as ArrayList<String>
                    val category=data["category"] as? Category
                   var arrayList=ArrayList<String>()
                    arrayList.addAll(images)
                    val product = Product(id, name, price, offerPercentage, description, sizes, arrayList,category)
                    productArrayList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                val builder =
                    AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("An error occurred")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
            }
    }
}