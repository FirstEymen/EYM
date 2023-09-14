package com.bilireymen.eym

import android.content.Context
import com.bilireymen.eym.models.User
import com.google.gson.Gson

class Utils {

    companion object {
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
        }

    }
}