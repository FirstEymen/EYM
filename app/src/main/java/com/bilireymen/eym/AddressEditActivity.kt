package com.bilireymen.eym

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddressEditActivity : AppCompatActivity() {

    private lateinit var editAddressNameLayout: TextInputLayout
    private lateinit var editAddressAddressLayout: TextInputLayout
    private lateinit var deleteEditAddressButton: TextView
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")
    private lateinit var selectedAddress: Address

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_edit)

        val selectedAddress = intent.getSerializableExtra("selectedAddress") as Address

        val editTextAddressName = findViewById<EditText>(R.id.editTextAddressName)
        val editTextAddress = findViewById<EditText>(R.id.editTextAddress)
        editAddressNameLayout = findViewById(R.id.editAddressNameLayout)
        editAddressAddressLayout = findViewById(R.id.editAddressAddressLayout)
         editTextAddressName.setText(selectedAddress.name)
         editTextAddress.setText(selectedAddress.address)

        val editSaveAddress = findViewById<TextView>(R.id.editSaveAddress)
        editSaveAddress.setOnClickListener {
            val newName = editTextAddressName.text.toString().trim()
            val newAddress = editTextAddress.text.toString().trim()

            if (newName.isEmpty() || newAddress.isEmpty()) {
                // Boş alan kontrolü
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (EYMAplication.getInstance().user != null) {
                // Güncellenmiş adres oluşturuluyor
                val updatedAddress = Address(newName, newAddress)
                // Sadece adresi güncelle ve Firestore'a kaydet
                usersCollection.document(EYMAplication.getInstance().user!!.id!!)
                    .update("addresses", FieldValue.arrayRemove(selectedAddress))
                    .addOnSuccessListener {
                        usersCollection.document(EYMAplication.getInstance().user!!.id!!)
                            .update("addresses", FieldValue.arrayUnion(updatedAddress))
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@AddressEditActivity,
                                    "The address has been successfully updated.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish() // Aktiviteyi kapat
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this@AddressEditActivity,
                                    "An error occurred while updating the address: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@AddressEditActivity,
                            "An error occurred while updating the address: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                // Intent'e adres bilgilerini ekleyerek geri dön
                val resultIntent = Intent()
                resultIntent.putExtra("selectedAddress", newAddress)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        deleteEditAddressButton = findViewById(R.id.deleteEditAddress)
        deleteEditAddressButton.setOnClickListener {
            deleteUserAddress(selectedAddress)
        }
    }

    private fun deleteUserAddress(address: Address) {
        val userId = EYMAplication.getInstance().user!!.id
        if (userId != null) {
            usersCollection.document(userId).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        val updatedAddresses = user.addresses?.filter { it.address != address.address }
                        val userData = hashMapOf<String, Any?>("addresses" to updatedAddresses)

                        val customView = LayoutInflater.from(this@AddressEditActivity).inflate(R.layout.custom_alert_dialog2, null)
                        val warningName = customView.findViewById<TextView>(R.id.warningName)
                        val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                        val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)
                        val warningBtn2 = customView.findViewById<TextView>(R.id.warningBtn2)

                        warningName.text = "Warning!"
                        warningDescription.text = "Are you sure you want to delete this address?"

                        val alertDialog = AlertDialog.Builder(this@AddressEditActivity)
                            .setView(customView)
                            .create()

                        warningBtn.text = "No"
                        warningBtn.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@AddressEditActivity, R.anim.bounce))
                            alertDialog.dismiss()
                        }

                        warningBtn2.text = "Yes"
                        warningBtn2.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@AddressEditActivity, R.anim.bounce))
                            alertDialog.dismiss()

                            // Kullanıcı "Yes" düğmesine tıkladığında adresi sil
                            usersCollection.document(userId)
                                .update(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@AddressEditActivity,
                                        "The address has been successfully deleted.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this@AddressEditActivity,
                                        "An error occurred while deleting the address: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                        alertDialog.show()
                    }
                }
            }
        }
    }

}
