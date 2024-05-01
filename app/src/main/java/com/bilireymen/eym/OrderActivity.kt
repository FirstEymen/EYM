package com.bilireymen.eym

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.adapter.OrderItemAdapter
import com.bilireymen.eym.adapter.OrderListItemAdapter
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.CartProduct
import com.bilireymen.eym.models.Checkout
import com.bilireymen.eym.models.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var orderListRv: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        firestore = FirebaseFirestore.getInstance()
        userId = EYMAplication.getUserId()

        orderListRv=findViewById<RecyclerView>(R.id.orderListRv)
        orderListRv.layoutManager=LinearLayoutManager(this)

        fetchOrderData(userId)

    }
    private fun fetchOrderData(userId: String) {
        firestore.collection("Orders")
            .document(userId)
            .collection("My Orders")
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val orderList = ArrayList<Order>()
                for (document in querySnapshot.documents) {

                    val order = document.toObject(Order::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }

                if (orderList.isNotEmpty()) {
                    val orderListAdapter=OrderListItemAdapter(orderList,this)
                    orderListRv.adapter=orderListAdapter

                } else {
                    // Sipariş bulunamadıysa kullanıcıya bilgi verin
                    Toast.makeText(
                        this@OrderActivity,
                        "Sipariş bulunamadı",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                // Hata durumunda işlem yapın
                Toast.makeText(
                    this@OrderActivity,
                    "Sipariş verilerini alırken hata oluştu: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}