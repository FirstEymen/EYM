package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bilireymen.eym.adapter.GridRvAdapter
import com.bilireymen.eym.databinding.ActivityCategoryproductsBinding
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore
class CategoryProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryproductsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapterGrid: GridRvAdapter
    private var productArrayList: ArrayList<Product> = ArrayList()
    private  var category: Category?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryproductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()
        category = intent.getSerializableExtra("category") as? Category
        category?.let {
            getData(it)
        }
        gridRvAdapter()
    }
    private fun gridRvAdapter() {
        adapterGrid = GridRvAdapter(this, productArrayList)
        binding.gridRVCategoryProducts.layoutManager = GridLayoutManager(this, 2)
        binding.gridRVCategoryProducts.adapter = adapterGrid
        adapterGrid.setOnItemClickListener(object : GridRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {
                // Ürün detayları sayfasına git
                val intent =
                    Intent(this@CategoryProductsActivity, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })
    }
    private fun getData(category: Category) {
        // Kategorinin altındaki ürünleri Firestore'dan al
        firestore.collection("Products")
            .whereEqualTo("category.id",category.id)
            .get()
            .addOnSuccessListener { result ->
                productArrayList.clear() // Önceki verileri temizle
                for (document in result) {
                    val data: Map<String, Any> = document.data
                    val id = data["id"] as? String
                    val name = data["name"] as? String
                    val price = data["price"] as? Double
                    val offerPercentage = data["offerPercentage"] as? Double
                    val description = data["description"] as? String
                    val sizes = data["sizes"] as? List<String>
                    val images = data["images"] as? ArrayList<String>
                    val categoryMap = data["category"] as? HashMap<String, Any>
                    // Tüm gerekli alanlar mevcut mu diye kontrol edin
                    if (id != null && name != null && price != null && offerPercentage != null && description != null && sizes != null && images != null && categoryMap != null) {
                        val category = Category.categoryHashMapToCategory(categoryMap)
                        val product = Product(id, name, price, offerPercentage, description, sizes, images, category)
                        productArrayList.add(product)
                    }
                }
                // Yeni verileri RecyclerView'e yükleyin
                binding.gridRVCategoryProducts.adapter!!.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get products: ${exception.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
