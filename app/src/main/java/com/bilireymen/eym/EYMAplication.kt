package com.bilireymen.eym

import android.app.Application
import android.content.Context

class EYMAplication: Application() {
    override fun onCreate() {
        super.onCreate()
        EYMAplication.appContext = applicationContext
    }

    companion object {

        lateinit  var appContext: Context

    }
}