package com.bilireymen.eym

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bilireymen.eym.databinding.ActivityShoppingBinding
import com.bilireymen.eym.eventbus.ProductListReceived
import com.bilireymen.eym.fragments.HomeFragment
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import org.greenrobot.eventbus.EventBus

class ShoppingActivity:AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    open var productArrayList:ArrayList<Product> = ArrayList()
    val binding by lazy{
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        firestore=FirebaseFirestore.getInstance()

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        getData()
    }

    private fun getData(){
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
                    val category: Category?=null
                    var arrayList=ArrayList<String>()
                    arrayList.addAll(images)
                    val product = Product(id, name, price, offerPercentage, description, sizes, arrayList,category)
                    productArrayList.add(product)
                    EventBus.getDefault().post(ProductListReceived())
                }
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