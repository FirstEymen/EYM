package com.bilireymen.eym

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
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
        @SuppressLint("HardwareIds")
        fun getUserId():String{
            return if (getInstance().user!=null&&!TextUtils.isEmpty(getInstance().user!!.id) )
                getInstance().user!!.id!!
            else Settings.Secure.getString(
                getInstance().contentResolver,
                Settings.Secure.ANDROID_ID)
        }
    }
}