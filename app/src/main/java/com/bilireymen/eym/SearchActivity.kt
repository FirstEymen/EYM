package com.bilireymen.eym

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bilireymen.eym.adapter.GridRvAdapter
import com.bilireymen.eym.databinding.ActivitySearchBinding
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class SearchActivity:AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapterGrid:GridRvAdapter
    var productArrayList:ArrayList<Product> = ArrayList()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchTextSearch:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchTextSearch.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        firestore= FirebaseFirestore.getInstance()

        searchTextSearch = binding.root.findViewById(R.id.searchTextSearch)
        val searchBarSearch = binding.root.findViewById<ConstraintLayout>(R.id.searchBarSearch)
        val underlineView = binding.root.findViewById<View>(R.id.underlineView)

        searchTextSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus || searchTextSearch.text.isEmpty()){
                startAnimation()
            }else{
                underlineView.visibility = View.GONE
            }
        }
        searchTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){
                // Do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                startAnimation()
                val searchText = s.toString().trim()
                searchProducts(searchText)
            }
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
        searchBarSearch.setOnClickListener {
            val searchText = searchTextSearch.text.toString().trim()
            searchProducts(searchText)
        }
        gridRvAdapter()
    }

    private fun startAnimation() {
        val underlineView = binding.underlineView
        underlineView.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(underlineView, "alpha", 1f, 0f)
        animator.duration = 500
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }

    private fun searchProducts(query: String) {
        if (query.isBlank()) {
            productArrayList.clear()
            adapterGrid.notifyDataSetChanged()
            return
        }
        firestore.collection("Products")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                productArrayList.clear()
                for (document in result) {
                    val data: Map<String, Any> = document.data
                    // Firestore'dan gelen ürünleri al
                    val id = data["id"] as String
                    val name = data["name"] as String
                    val price = data["price"] as Double
                    val offerPercentage = data["offerPercentage"] as Double
                    val description = data["description"] as String
                    val sizes = data["sizes"] as List<String>
                    val images = data["images"] as ArrayList<String>
                    val category = Category.categoryHashMapToCategory(data["category"] as HashMap<String, Any>)
                    val product = Product(id, name, price, offerPercentage, description, sizes, images, category)
                    productArrayList.add(product)
                }
                adapterGrid.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Hata durumunda bildirim göster
                Toast.makeText(this, "An error occurred: $it", Toast.LENGTH_SHORT).show()
            }
    }
    private fun gridRvAdapter(){
        adapterGrid = GridRvAdapter(this, productArrayList ?: arrayListOf())
        binding.gridRvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.gridRvSearch.adapter = adapterGrid
        adapterGrid.setOnItemClickListener(object : GridRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(this@SearchActivity, ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })
    }

}
