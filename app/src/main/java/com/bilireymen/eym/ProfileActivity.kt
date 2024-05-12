package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bilireymen.eym.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class ProfileActivity:AppCompatActivity() {

    private lateinit var profileNameLayout: TextInputLayout
    private lateinit var profileLastNameLayout: TextInputLayout
    private lateinit var profileEmailLayout: TextInputLayout
    private lateinit var profilePhoneLayout: TextInputLayout
    private lateinit var editProfileName: EditText
    private lateinit var editProfileLastName: EditText
    private lateinit var editProfileEmail: EditText
    private lateinit var editProfilePhone: EditText
    private lateinit var profileSave: TextView
    private lateinit var currentUser: User
    private val firestoreHelper = FirestoreHelper()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var usersCollection: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firestore=FirebaseFirestore.getInstance()
        usersCollection = firestore.collection("Users")

        editProfileName = findViewById(R.id.editProfileName)
        editProfileLastName = findViewById(R.id.editProfileLastName)
        editProfileEmail = findViewById(R.id.editProfileEmail)
        editProfilePhone = findViewById(R.id.editProfilePhone)
        profileNameLayout = findViewById(R.id.profileNameLayout)
        profileLastNameLayout = findViewById(R.id.profileLastNameLayout)
        profileEmailLayout = findViewById(R.id.profileEmailLayout)
        profilePhoneLayout = findViewById(R.id.profileLastNameLayout)
        profileSave = findViewById(R.id.profileSave)

        val currentUser = EYMAplication.getInstance().user

        if (currentUser != null) {
            displayUserInfo(currentUser)

            profileSave.setOnClickListener {
                val newName = editProfileName.text.toString()
                val newLastName = editProfileLastName.text.toString()
                val newPhone = editProfilePhone.text.toString()

                // Güncellenecek alanları bir harita olarak oluştur
                val updatedFields = mutableMapOf<String, Any>()
                if (newName.isNotEmpty()) updatedFields["firstName"] = newName
                if (newLastName.isNotEmpty()) updatedFields["lastName"] = newLastName
                if (newPhone.isNotEmpty()) updatedFields["phone"] = newPhone

                // Firestore'da kullanıcıyı güncelle
                firestoreHelper.updateUser(currentUser.id!!, updatedFields,
                    onSuccess = {

                        // Güncelleme başarılıysa, yerel kullanıcı nesnesini de güncelle
                        EYMAplication.getInstance().user?.let { user ->
                            updatedFields.forEach { (key, value) ->
                                when (key) {
                                    "firstName" -> user.firstName = value as String
                                    "lastName" -> user.lastName = value as String
                                    "phone" -> user.phone = value as String
                                }
                            }
                        }
                        val customView = LayoutInflater.from(this@ProfileActivity).inflate(R.layout.custom_alert_dialog, null)
                        val alertDialog = AlertDialog.Builder(this@ProfileActivity)
                            .setView(customView)
                            .create()

                        val warningName = customView.findViewById<TextView>(R.id.warningName)
                        val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
                        val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)

                        warningName.text = "Notification"
                        warningDescription.text = "Profile updated successfully."
                        warningBtn.text = "OK"
                        warningBtn.setOnClickListener {
                            customView.startAnimation(AnimationUtils.loadAnimation(this@ProfileActivity, R.anim.bounce))
                            alertDialog.dismiss()
                        }

                        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                        alertDialog.show()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Profil güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }else{
            // Kullanıcı verileri alınamadıysa burası çalışır
            Toast.makeText(this, "Kullanıcı verileri alınamadı", Toast.LENGTH_SHORT).show()
        }

        val deleteUserProfile = findViewById<TextView>(R.id.deleteUserProfile)
        deleteUserProfile.setOnClickListener {
            val customView = LayoutInflater.from(this@ProfileActivity).inflate(R.layout.custom_alert_dialog2, null)
            val warningName = customView.findViewById<TextView>(R.id.warningName)
            val warningDescription = customView.findViewById<TextView>(R.id.warningDescription)
            val warningBtn = customView.findViewById<TextView>(R.id.warningBtn)
            val warningBtn2 = customView.findViewById<TextView>(R.id.warningBtn2)

            warningName.text = "Warning!"
            warningDescription.text = "Are you sure you want to delete your account? This action cannot be undone."

            val alertDialog = AlertDialog.Builder(this@ProfileActivity)
                .setView(customView)
                .create()
            warningBtn.text = "No"
            warningBtn.setOnClickListener {
                customView.startAnimation(AnimationUtils.loadAnimation(this@ProfileActivity, R.anim.bounce))
                alertDialog.dismiss()
            }
            warningBtn2.text = "Yes"
            warningBtn2.setOnClickListener {
                customView.startAnimation(AnimationUtils.loadAnimation(this@ProfileActivity, R.anim.bounce))
                alertDialog.dismiss()
                // Kullanıcı "Yes" düğmesine tıkladığında hesabı sil
                deleteUserAccount(currentUser!!)
            }
            alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            alertDialog.show()
        }

    }

    private fun displayUserInfo(user: User) {
        editProfileName.setText(user.firstName)
        editProfileLastName.setText(user.lastName)
        editProfileEmail.setText(user.email)
        editProfilePhone.setText(user.phone)
    }

    private fun deleteUserAccount(user: User) {
        val userId = user.id
        if (userId != null) {
            usersCollection.document(userId)
                .delete()
                .addOnSuccessListener {
                    // Hesap başarıyla silindiğinde kullanıcı bilgilerini temizle
                    clearUserData()
                    signOut()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@ProfileActivity,
                        "An error occurred while deleting the account: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
    private fun clearUserData() {
        // Kullanıcı bilgilerini SharedPreferences'ten temizle
        Utils.clearUserDataFromSharedPreferences(this)

        // Kullanıcı nesnesini null yaparak EYMAplication'daki kullanıcıyı temizle
        EYMAplication.getInstance().user = null

        // Oturumu kapat ve IntroductionActivity'e yönlendir
        signOut()
    }

    private fun signOut() {
        // Oturumu kapat ve IntroductionActivity'e yönlendir
        val intent = Intent(this, IntroductionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}