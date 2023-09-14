package com.bilireymen.eym.fragments

import CartItemAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.EYMAplication
import com.bilireymen.eym.R
import com.bilireymen.eym.models.CartProduct
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment(R.layout.fragment_cart),CartItemAdapter.OnCartUpdateListener {
    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var cartItemsRecyclerView: RecyclerView
    private var cartProducts: ArrayList<CartProduct> = ArrayList()
    private lateinit var firebaseFirestore: FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseFirestore = FirebaseFirestore.getInstance()

        val androidId = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        cartItemsRecyclerView = view.findViewById<RecyclerView>(R.id.cartRv)
        cartItemAdapter = CartItemAdapter(requireContext(), cartProducts,firebaseFirestore,androidId,this) // Adaptörü güncellendi
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartItemAdapter
        cartItemAdapter.setOnCartUpdateListener(this)

        firebaseFirestore.collection("Cart").document(androidId!!)
            .collection("Cart Products")
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    val data = document.data
                    val productData = data["product"] as Map<String, Any>
                    val product = Product(
                        productData["id"] as String ,
                        productData["name"] as String,
                        productData["price"] as Double,
                        productData["offerPercentage"] as Double,
                        productData["description"] as String,
                        productData["sizes"] as  List<String>,
                        productData["images"]  as ArrayList<String>,
                       if (productData["category"] !=null) productData["category"] as Category else null,
                    )
                    val quantity = data["quantity"] as Long
                    val selectedSize = if (data["selectedSize"]!=null) data["selectedSize"] as String else null
                    val cartProduct = CartProduct(product, quantity.toInt(), selectedSize)
                    cartProducts.add(cartProduct)
                }
                var totalCartPrice = 0.0
                for (cartProduct in cartProducts) {
                    totalCartPrice += cartProduct.product.price!! * cartProduct.quantity!!
                }
                val subtotalPriceTextView = view.findViewById<TextView>(R.id.subtotalPrice)
                subtotalPriceTextView.text = "$${String.format("%.2f", totalCartPrice)}"
                cartItemAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {  val builder =
                AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("An error occurred")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        EYMAplication.appContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
            }

    }

    private fun updateSubtotalPrice() {
        var totalCartPrice = 0.0
        for (cartProduct in cartProducts) {
            totalCartPrice += cartProduct.product.price!! * cartProduct.quantity!!
        }
        val subtotalPriceTextView = view?.findViewById<TextView>(R.id.subtotalPrice)
        subtotalPriceTextView?.text = "$${String.format("%.2f", totalCartPrice)}"
    }
    override fun onCartUpdated() {
        // Sepet güncellendiğinde burası çalışacak
        updateSubtotalPrice()
    }
}








