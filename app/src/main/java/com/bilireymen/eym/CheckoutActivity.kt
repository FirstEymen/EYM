package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.adapter.CheckoutAddressItemAdapter
import com.bilireymen.eym.adapter.CheckoutItemAdapter
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.CartProduct
import com.bilireymen.eym.models.Checkout
import com.bilireymen.eym.models.Order
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartProducts: ArrayList<CartProduct>
    private lateinit var selectedAddress: Address
    private lateinit var recyclerView: RecyclerView
    private lateinit var addressRecyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var checkoutId: String
    private lateinit var subtotalPriceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        cartProducts = intent.getSerializableExtra("cartProducts") as ArrayList<CartProduct>
        selectedAddress = intent.getSerializableExtra("selectedAddress") as Address
        userId = EYMAplication.getUserId()

        recyclerView = findViewById(R.id.checkoutProductRv)
        addressRecyclerView = findViewById(R.id.checkoutAddressRv)
        subtotalPriceTextView = findViewById(R.id.subtotalPriceCheckout)

        val checkoutAdapter = CheckoutItemAdapter(cartProducts, subtotalPriceTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = checkoutAdapter

        val addressAdapter = CheckoutAddressItemAdapter(selectedAddress)
        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        addressRecyclerView.adapter = addressAdapter

        firestore = FirebaseFirestore.getInstance()

        val checkout = Checkout(cartProducts, selectedAddress,EYMAplication.getUserId())
        saveCheckoutToFirestore(checkout)

        val completeOrderBtn=findViewById<TextView>(R.id.completeOrderBtn)
        completeOrderBtn.setOnClickListener{
            if(EYMAplication.getInstance().user!=null) {

                val order = Order(id = null, checkout,System.currentTimeMillis())
                saveOrderToFirestore(order, checkoutId)
                deleteCheckoutFromFirestore(checkoutId)
            }else{

            }
        }

    }

    private fun saveCheckoutToFirestore(checkout: Checkout) {
        firestore.collection("Checkout")
            .add(checkout)
            .addOnSuccessListener { documentReference ->
                checkoutId = documentReference.id // checkoutId'yi kaydedin
                val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)
                val alertDialog = AlertDialog.Builder(this).setView(customView).create()

                val warningName = customView.findViewById<TextView>(R.id.warningName)
                val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)

                warningName.text = "Notification"
                warningDescription.text="Checkout has been registered successfully."

                warningBtn.setOnClickListener {
                    customView.startAnimation(AnimationUtils.loadAnimation(this@CheckoutActivity, R.anim.bounce))
                    alertDialog.dismiss()
                }
                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
            .addOnFailureListener { e ->
                val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)
                val alertDialog = AlertDialog.Builder(this).setView(customView).create()

                val warningName = customView.findViewById<TextView>(R.id.warningName)
                val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)

                warningName.text = "Notification"
                warningDescription.text="An error occurred while registering the checkout."

                warningBtn.setOnClickListener {
                    customView.startAnimation(AnimationUtils.loadAnimation(this@CheckoutActivity, R.anim.bounce))
                    alertDialog.dismiss()
                }
                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
    }

    private fun deleteCheckoutFromFirestore(checkoutId: String) {
        firestore.collection("Checkout")
            .document(checkoutId)
            .delete()
            .addOnSuccessListener {
                val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)
                val alertDialog = AlertDialog.Builder(this).setView(customView).create()

                val warningName = customView.findViewById<TextView>(R.id.warningName)
                val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)

                warningName.text = "Notification"
                warningDescription.text="Checkout has been deleted successfully."

                warningBtn.setOnClickListener {
                    customView.startAnimation(AnimationUtils.loadAnimation(this@CheckoutActivity, R.anim.bounce))
                    alertDialog.dismiss()
                }
                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
            .addOnFailureListener { e ->
                val customView = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)
                val alertDialog = AlertDialog.Builder(this).setView(customView).create()

                val warningName = customView.findViewById<TextView>(R.id.warningName)
                val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)

                warningName.text = "Notification"
                warningDescription.text="An error occurred while deleting the checkout."

                warningBtn.setOnClickListener {
                    customView.startAnimation(AnimationUtils.loadAnimation(this@CheckoutActivity, R.anim.bounce))
                    alertDialog.dismiss()
                }
                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
    }

    private fun saveOrderToFirestore(order: Order, checkoutId: String) {
        firestore.collection("Orders").document(userId).collection("My Orders")
            .add(order)
            .addOnSuccessListener { documentReference ->
                val orderId = documentReference.id
                order.id = orderId

                firestore.collection("Orders").document(userId).collection("My Orders")
                    .document(orderId)
                    .set(order)
                    .addOnSuccessListener {
                        // Order başarıyla kaydedildi, şimdi Checkout'u sil.
                        deleteCheckoutFromFirestore(checkoutId)
                    }

                val intent = Intent(this@CheckoutActivity, OrderActivity::class.java)
                startActivity(intent)
                Toast.makeText(
                    this@CheckoutActivity,
                    "Order has been registered successfully. Document ID: $orderId",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@CheckoutActivity,
                    "An error occurred while registering the order: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}