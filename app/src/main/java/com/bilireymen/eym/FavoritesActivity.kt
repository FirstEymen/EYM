package com.bilireymen.eym

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilireymen.eym.adapter.GridRvAdapter
import com.bilireymen.eym.adapter.HorizontalRvItemAdapter
import com.bilireymen.eym.databinding.ActivityFavoritesBinding
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity: AppCompatActivity() {
    private lateinit var binding:ActivityFavoritesBinding
    private var productArrayList: ArrayList<Product> = ArrayList()
    private lateinit var adapterGrid: GridRvAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        listFavoriteProducts()
    }
    private fun listFavoriteProducts() {
        // Favori ürünlerin Id'lerini al
        val favoriteProductIds = CommonFunctions.getFavoriteProductList()
        for (productId in favoriteProductIds) {
            // Ürünü Firestore'dan alıp, callback ile işlemleri gerçekleştir
            getProductById(productId) { product ->
                product?.let {
                    productArrayList.add(it)
                    gridRvAdapter()
                }
            }
        }
    }
    private fun getProductById(productId: String, callback: (Product?) -> Unit) {
        firestore.collection("Products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Product::class.java)
                    // Geri dönüş değerini callback aracılığıyla iletebiliriz
                    callback(product)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting product", exception)
                callback(null) // Hata durumunda null döndür
            }
    }
    private fun gridRvAdapter() {
        adapterGrid = GridRvAdapter(this, productArrayList)
        binding.favoritesProfile.layoutManager = GridLayoutManager(this, 2)
        binding.favoritesProfile.adapter = adapterGrid
        adapterGrid.setOnItemClickListener(object : GridRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {
                // Ürün detayları sayfasına git
                val intent =
                    Intent(this@FavoritesActivity, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })
    }
}