package com.bilireymen.eym

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore


class AddAddressActivity : AppCompatActivity() {

    private lateinit var addAddressName: TextInputEditText
    private lateinit var addAddressAddress: TextInputEditText
    private lateinit var buttonSaveAddress: Button
    private lateinit var addAddressNameLayout:TextInputLayout

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_add)

        addAddressName = findViewById(R.id.addAddressName)
        addAddressAddress = findViewById(R.id.addAddressAddress)
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress)
        addAddressNameLayout=findViewById(R.id.addAddressNameLayout)

        buttonSaveAddress.setOnClickListener {

            if (EYMAplication.getInstance().user!=null) {

                val addressName = addAddressName.text.toString()
                val addressText = addAddressAddress.text.toString()

                // Yeni bir adres oluştur
                val newAddress = Address(
                    addressName,
                    addressText
                )

                // Kullanıcının mevcut adres listesini al
                usersCollection.document(EYMAplication.getInstance().user!!.id!!).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val user = documentSnapshot.toObject(User::class.java)
                            if (user != null) {
                                // Adresleri kullanıcının adres listesine ekleyin
                                user.addresses?.add(newAddress)

                                // Adres listesini Firestore'a güncelleyin
                                usersCollection.document(EYMAplication.getInstance().user!!.id!!).update("addresses", user.addresses)
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
            } else{
                val addressName = addAddressName.text.toString()
                val addressText = addAddressAddress.text.toString()

                val newAddress = Address(
                    addressName,
                    addressText
                )
                // Intent'e adres bilgilerini ekleyerek geri dön
                val resultIntent = Intent()
                resultIntent.putExtra("selectedAddress", newAddress)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

    }
}