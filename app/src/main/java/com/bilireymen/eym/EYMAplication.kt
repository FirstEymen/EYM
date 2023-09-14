package com.bilireymen.eym

import android.app.Application
import android.content.Context
import com.bilireymen.eym.models.User

class EYMAplication : Application() {

    var user: User? = null

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        instance = this  // EYMAplication sınıfının örneğini burada başlatın
    }

    companion object {
        lateinit var appContext: Context
        private lateinit var instance: EYMAplication

        fun getInstance(): EYMAplication {
            return instance
        }
    }
}