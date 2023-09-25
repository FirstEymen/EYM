package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.bilireymen.eym.models.User
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
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseFirestore= FirebaseFirestore.getInstance()
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EYMAplication.getUserId()

        user = Utils.getUserFromSharedPreferences(this)

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

            if (user != null) { // Kullanıcı giriş yapmışsa Firestore kullan
                val userId = EYMAplication.getUserId()
                var selectedSize: String? = sizesAdapter.selectedSize
                val cartProduct = CartProduct(product!!, currentQuantity, selectedSize.toString())
                val cartProductMap = hashMapOf(
                    "product" to cartProduct.product,
                    "quantity" to cartProduct.quantity,
                    "selectedSize" to cartProduct.selectedSize
                )
                val userCartCollection = firebaseFirestore.collection("Cart").document(userId!!)
                    .collection("Cart Products")

                userCartCollection.document(product!!.id!!).set(cartProductMap)
                    .addOnSuccessListener {

                        val customView = LayoutInflater.from(this@ProductDetailsActivity).inflate(R.layout.custom_alert_dialog2, null)
                        val alertDialog = AlertDialog.Builder(this@ProductDetailsActivity)
                            .setView(customView)
                            .create()

                        val warningName = customView.findViewById<TextView>(R.id.warningName)
                        val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                        val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)
                        val warningBtn2 = customView.findViewById<TextView>(R.id.warningBtn2)

                        warningName.text = "Notification"
                        warningDescription.text = "The product has been added to the cart, would you like to go to the cart?"

                        warningBtn.text = "CANCEL"
                        warningBtn.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@ProductDetailsActivity, R.anim.bounce))
                            alertDialog.dismiss()
                        }

                        warningBtn2.text = "OK"
                        warningBtn2.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@ProductDetailsActivity, R.anim.bounce))
                            alertDialog.dismiss()

                            val intent = Intent(this@ProductDetailsActivity, CartActivity::class.java)
                            startActivity(intent)
                        }

                        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                        alertDialog.show()
                    }
                    .addOnFailureListener { e ->
                        // Hata durumunda yapılacak işlemler
                    }
            } else {
                var selectedSize: String? = sizesAdapter.selectedSize
                // Kullanıcı giriş yapmamışsa Android ID ile ekle
                val cartProduct = CartProduct(product!!, currentQuantity, selectedSize!!)
                val cartProductMap = hashMapOf(
                    "product" to cartProduct.product,
                    "quantity" to cartProduct.quantity,
                    "selectedSize" to cartProduct.selectedSize
                )
                val cartCollection = firebaseFirestore.collection("Cart").document(androidId)
                    .collection("Cart Products")

                cartCollection.document(product!!.id!!).set(cartProductMap)
                    .addOnSuccessListener {

                        val customView = LayoutInflater.from(this@ProductDetailsActivity).inflate(R.layout.custom_alert_dialog2, null)
                        val alertDialog = AlertDialog.Builder(this@ProductDetailsActivity)
                            .setView(customView)
                            .create()

                        val warningName = customView.findViewById<TextView>(R.id.warningName)
                        val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                        val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)
                        val warningBtn2 = customView.findViewById<TextView>(R.id.warningBtn2)

                        warningName.text = "Notification"
                        warningDescription.text = "The product has been added to the cart, would you like to go to the cart?"

                        warningBtn.text = "CANCEL"
                        warningBtn.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@ProductDetailsActivity, R.anim.bounce))
                            alertDialog.dismiss()
                        }

                        warningBtn2.text = "OK"
                        warningBtn2.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@ProductDetailsActivity, R.anim.bounce))
                            alertDialog.dismiss()

                            val intent = Intent(this@ProductDetailsActivity, CartActivity::class.java)
                            startActivity(intent)
                        }

                        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                        alertDialog.show()
                    }
                    .addOnFailureListener { e ->
                        // Hata durumunda yapılacak işlemler
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


    /*fun showCustomAlertDialog() {
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog2, null)
        val alertDialog = AlertDialog.Builder(this).setView(customView).create()

        // Özel AlertDialog içindeki elemanları tanımla
        val warningName = customView.findViewById<TextView>(R.id.warningName)
        val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
        val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)
        val warningBtn2 = customView.findViewById<TextView>(R.id.warningBtn2)

        // TextView'e metin atama
        warningName.text = "Warning"
        warningDescription.text="Email field cannot beempty"
        // Butona tıklama işlevi ekle
        warningBtn.setOnClickListener {
            alertDialog.dismiss() // AlertDialog'ı kapat
        }
        warningBtn2.setOnClickListener {
            alertDialog.dismiss() // AlertDialog'ı kapat
        }

        alertDialog.show()
    }

     */
}