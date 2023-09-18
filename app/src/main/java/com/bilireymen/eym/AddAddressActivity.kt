package com.bilireymen.eym

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddAddressActivity : AppCompatActivity() {

    private lateinit var addAddressName: TextInputEditText
    private lateinit var addAddressAddress: TextInputEditText
    private lateinit var buttonSaveAddress: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_add)

        addAddressName = findViewById(R.id.addAddressName)
        addAddressAddress = findViewById(R.id.addAddressAddress)
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress)

        buttonSaveAddress.setOnClickListener {

            val userId = Utils.getUserFromSharedPreferences(this)?.id

            if (userId != null) {
                val addressName = addAddressName.text.toString()
                val addressText = addAddressAddress.text.toString()

                // Yeni bir adres oluştur
                val newAddress = Address(
                    name = addressName,
                    address = addressText
                )

                // Kullanıcının mevcut adres listesini al
                usersCollection.document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val user = documentSnapshot.toObject(User::class.java)
                            if (user != null) {
                                // Adresleri kullanıcının adres listesine ekleyin
                                user.addresses?.add(newAddress)

                                // Adres listesini Firestore'a güncelleyin
                                usersCollection.document(userId).update("addresses", user.addresses)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@AddAddressActivity,
                                            "The address has been successfully registered.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish() // Aktiviteyi kapat
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@AddAddressActivity,
                                            "An error occurred while saving the address: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@AddAddressActivity,
                            "An error occurred while retrieving user information: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this@AddAddressActivity,
                    "Failed to retrieve user ID.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}