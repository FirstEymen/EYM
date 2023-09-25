package com.bilireymen.eym.fragments

import CartItemAdapter
import android.app.Activity
import android.content.Intent
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
import com.bilireymen.eym.AddAddressActivity
import com.bilireymen.eym.AddressListActivity
import com.bilireymen.eym.CheckoutActivity
import com.bilireymen.eym.EYMAplication
import com.bilireymen.eym.R
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.CartProduct
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.bilireymen.eym.models.User
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment(R.layout.fragment_cart), CartItemAdapter.OnCartUpdateListener {

    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var cartItemsRecyclerView: RecyclerView
    private var cartProducts: ArrayList<CartProduct> = ArrayList()
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var user: User? = null
    private var selectedAddress: Address? = null
    private val ADDRESS_LIST_REQUEST_CODE = 1
    private val ADDRESS_DETAIL_REQUEST_CODE = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseFirestore = FirebaseFirestore.getInstance()

        val userId = EYMAplication.getUserId()

        cartItemsRecyclerView = view.findViewById<RecyclerView>(R.id.cartRv)
        cartItemAdapter = CartItemAdapter(requireContext(), cartProducts, firebaseFirestore, userId, this)
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemsRecyclerView.adapter = cartItemAdapter
        cartItemAdapter.setOnCartUpdateListener(this)

        firebaseFirestore.collection("Cart").document(userId)
            .collection("Cart Products")
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    val data = document.data
                    val productData = data["product"] as Map<String, Any>
                    val product = Product(
                        productData["id"] as String,
                        productData["name"] as String,
                        productData["price"] as Double,
                        productData["offerPercentage"] as Double,
                        productData["description"] as String,
                        productData["sizes"] as List<String>,
                        productData["images"] as ArrayList<String>,
                        if (productData["category"] != null) {
                            Category.categoryHashMapToCategory(productData["category"] as HashMap<String, Any>)
                        } else {
                            null
                        },
                    )
                    val quantity = data["quantity"] as Long
                    val selectedSize = if (data["selectedSize"] != null) data["selectedSize"] as String else null
                    val cartProduct = CartProduct(product, quantity.toInt(), selectedSize!!)
                    cartProducts.add(cartProduct)
                }
                updateSubtotalPrice()
                cartItemAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                val builder =
                    AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("An error occurred: ${e.message}")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        EYMAplication.appContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
            }

        val checkoutButton = view.findViewById<TextView>(R.id.checkoutBtn)
        checkoutButton.setOnClickListener {
            if(EYMAplication.getInstance().user!=null) {
                val intent = Intent(requireContext(), AddressListActivity::class.java)
                startActivityForResult(intent, ADDRESS_LIST_REQUEST_CODE)
            }else {
                val intent=Intent(requireContext(),AddAddressActivity::class.java)
                startActivityForResult(intent,ADDRESS_DETAIL_REQUEST_CODE)
            }
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
        updateSubtotalPrice()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADDRESS_LIST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data != null && data.hasExtra("selectedAddress")) {
                val selectedAddress = data.getSerializableExtra("selectedAddress") as Address
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                intent.putExtra("selectedAddress", selectedAddress)
                intent.putExtra("cartProducts", cartProducts)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select an address.", Toast.LENGTH_SHORT).show()
            }
        }else if (requestCode==ADDRESS_DETAIL_REQUEST_CODE  && resultCode == Activity.RESULT_OK){
            if (data != null && data.hasExtra("selectedAddress")) {
                val selectedAddress = data.getSerializableExtra("selectedAddress") as Address
                val intent=Intent(requireContext(),CheckoutActivity::class.java)
                intent.putExtra("selectedAddress",selectedAddress)
                intent.putExtra("cartProducts",cartProducts)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please provide an address name and address.", Toast.LENGTH_SHORT).show()
            }

        }
    }

}







