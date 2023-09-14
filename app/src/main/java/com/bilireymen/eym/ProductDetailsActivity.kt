package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.ContextThemeWrapper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bilireymen.eym.adapter.SizesAdapter
import com.bilireymen.eym.adapter.ViewPager2Images
import com.bilireymen.eym.databinding.ActivityProductDetailsBinding
import com.bilireymen.eym.models.CartProduct
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsActivity:AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private var indicatorDots = mutableListOf<ImageView>()
    private var currentQuantity = 1
    private lateinit var tvQuantity: TextView
    private lateinit var btnDecrease: TextView
    private lateinit var btnIncrease: TextView
    private  var product: Product?=null
    private lateinit var firebaseFirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseFirestore= FirebaseFirestore.getInstance()
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getSerializableExtra("product") as? Product

        setupViewpager()
        setupSizesRv()

        binding.apply {
            productDetailsName.text = product?.name
            val oldPrice = "${"$" + product?.offerPercentage}"
            val spannable = SpannableString(oldPrice)
            spannable.setSpan(
                StrikethroughSpan(),
                0,
                oldPrice.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            productDetailsOldPrice.text = spannable
            val price = "${"$" + product?.price}"
            productDetailsPrice.text = price
            productDetailsPrice.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.red
                )
            )
            productDetailsDescription.text = product?.description
        }
        viewPagerAdapter.differ.submitList(product?.images)
        product?.sizes?.let { sizesAdapter.differ.submitList(it) }

        viewPager = findViewById(R.id.viewPagerProductImages)
        indicatorLayout = findViewById(R.id.indicatorLayout)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
        setupIndicator()

        tvQuantity = findViewById(R.id.tvQuantity)
        btnDecrease = findViewById(R.id.btnDecrease)
        btnIncrease = findViewById(R.id.btnIncrease)

        updateQuantityDisplay(currentQuantity)

        btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                updateQuantityDisplay(currentQuantity)
            }
        }

        btnIncrease.setOnClickListener {
            currentQuantity++
            updateQuantityDisplay(currentQuantity)
        }

        val buttonAddtoCart = findViewById<TextView>(R.id.buttonAddtoCart)
        buttonAddtoCart.setOnClickListener {

            val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            if (product != null) {
                var selectedSize: String? = sizesAdapter.selectedSize
                val cartProduct = CartProduct(product!!, currentQuantity, selectedSize)
                firebaseFirestore.collection("Cart").document(androidId!!).collection("Cart Products")
                    .document(product!!.id!!).set(cartProduct).addOnSuccessListener {

                    val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    builder.setTitle("Notification")
                    builder.setMessage("The product has been added to the cart, would you like to go to the cart?")
                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        // "Evet" düğmesine basıldığında yapılacak işlemler
                        Toast.makeText(
                            applicationContext,
                            android.R.string.yes, Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ProductDetailsActivity, CartActivity::class.java)
                        startActivity(intent)
                    }
                    builder.setNegativeButton(android.R.string.no) { dialog, which ->
                        // "Hayır" düğmesine basıldığında yapılacak işlemler
                        Toast.makeText(
                            applicationContext,
                            android.R.string.no, Toast.LENGTH_SHORT
                        ).show()
                    }
                    builder.show()
                }
            }
        }


    }

    private fun updateQuantityDisplay(quantity: Int) {
        tvQuantity.text = quantity.toString()
    }

    private fun setupIndicator() {
        val pageCount = viewPagerAdapter.itemCount
        for (i in 0 until pageCount) {
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.ic_dot_inactive)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            indicatorLayout.addView(dot)
            indicatorDots.add(dot)
        }
        updateIndicator(0)
    }

    private fun updateIndicator(selectedIndex: Int) {
        for (i in indicatorDots.indices) {
            val drawableId = if (i == selectedIndex) R.drawable.ic_dot_active else R.drawable.ic_dot_inactive
            indicatorDots[i].setImageResource(drawableId)
        }
    }

    private fun setupViewpager() {
        binding.viewPagerProductImages.adapter = viewPagerAdapter
    }

    private fun setupSizesRv() {
        binding.rvSize.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(
                this@ProductDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

}