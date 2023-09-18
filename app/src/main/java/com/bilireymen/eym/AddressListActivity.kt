package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.adapter.AddressItemAdapter
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.User
import com.google.firebase.firestore.FirebaseFirestore


class AddressListActivity : AppCompatActivity() {

    private lateinit var addressRecyclerView: RecyclerView
    private lateinit var addressAdapter: AddressItemAdapter
    private val addressList: MutableList<Address> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adress_list)

        addressRecyclerView = findViewById(R.id.addressRv)
        addressRecyclerView.layoutManager = LinearLayoutManager(this)
        addressAdapter = AddressItemAdapter(this, addressList)
        addressRecyclerView.adapter = addressAdapter

        val addressAddImageView = findViewById<ImageView>(R.id.addressAdd)
        addressAddImageView.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }

        val firestore = FirebaseFirestore.getInstance()
        val userId = Utils.getUserFromSharedPreferences(this)?.id

        if (userId != null) {
            val usersCollection = firestore.collection("Users")
            val userDocument = usersCollection.document(userId)

            userDocument.addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)

                    if (user != null && user.addresses != null) {
                        addressList.clear()
                        addressList.addAll(user.addresses)

                        // Kullanıcının seçtiği adres pozisyonunu Firestore veya SharedPreferences'ten alın
                        Utils.getUserSelectedAddressPositionFromDatabaseOrSharedPreferences(
                            this@AddressListActivity,
                            userId
                        ) { selectedPosition ->
                            addressAdapter.setSelectedAddressPosition(selectedPosition)
                        }

                        addressAdapter.notifyDataSetChanged()

                        userDocument.update("addresses", addressList)
                    }
                }
            }
        }
    }
}




