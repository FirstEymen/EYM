package com.bilireymen.eym

import android.content.Context
import com.bilireymen.eym.models.Address
import com.bilireymen.eym.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class Utils {

    companion object {
        fun getUserSelectedAddressPositionFromDatabaseOrSharedPreferences(context: Context, userId: String, callback: (Int) -> Unit) {

            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("Users")
            val userDocument = usersCollection.document(userId)

            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    val selectedPosition = documentSnapshot.getLong("selectedAddressPosition")
                    // Firestore'da kaydedilmişse bu değeri kullanabiliriz
                    if (selectedPosition != null) {
                        callback(selectedPosition.toInt())
                    } else {
                        // Firestore'da kayıtlı değilse, SharedPreferences'ten alabiliriz
                        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val position = sharedPreferences.getInt("selectedAddressPosition", -1)
                        callback(position)
                    }
                }
                .addOnFailureListener { e ->
                    // Firestore'dan veri alınamazsa, SharedPreferences'ten alabiliriz
                    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val position = sharedPreferences.getInt("selectedAddressPosition", -1)
                    callback(position)
                }
        }
        fun saveUserSelectedAddressPositionToDatabaseOrSharedPreferences(context: Context, userId: String, selectedPosition: Int) {

            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("Users")
            val userDocument = usersCollection.document(userId)

            userDocument.update("selectedAddressPosition", selectedPosition)
                .addOnSuccessListener {
                    // Firestore'a kaydedildi, SharedPreferences'i güncellemeye gerek yok
                }
                .addOnFailureListener { e ->
                    // Firestore'a kaydedilemezse, SharedPreferences'e kaydedebilirsiniz
                    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("selectedAddressPosition", selectedPosition)
                    editor.apply()
                }
        }

        // Kullanıcı bilgilerini SharedPreferences'e kaydeden metod
        fun saveUserDataToSharedPreferences(context: Context, user: User) {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Kullanıcı nesnesini JSON formatına çevirin
            val gson = Gson()
            val userJson = gson.toJson(user)

            // JSON formatındaki kullanıcı verisini SharedPreferences'e kaydedin
            editor.putString("userJson", userJson)
            editor.apply()
            EYMAplication.getInstance().user=user
        }

        // Kullanıcı bilgilerini SharedPreferences'den almak için bu kodu kullanabilirsiniz
            fun getUserFromSharedPreferences(context: Context): User? {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val userJson = sharedPreferences.getString("userJson", null)

            // JSON formatındaki kullanıcı verisini kullanıcı nesnesine dönüştürün
            return gson.fromJson(userJson, User::class.java)
        }

        // Kullanıcı bilgilerini SharedPreferences'ten temizlemek için
        fun clearUserDataFromSharedPreferences(context: Context) {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("userJson")
            editor.apply()
            EYMAplication.getInstance().user=null
        }


    }

}
