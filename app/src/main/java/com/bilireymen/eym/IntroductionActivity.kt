package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
class IntroductionActivity:AppCompatActivity() {
    private lateinit var buttonRegisterIntroduction: Button
    private lateinit var buttonLoginIntroduction: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        buttonRegisterIntroduction = findViewById(R.id.buttonRegisterAccountOptions)
        buttonLoginIntroduction = findViewById(R.id.buttonLoginAccountOptions)
        buttonRegisterIntroduction.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        buttonLoginIntroduction.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, ShoppingActivity::class.java)
        startActivity(intent)
        finish()
    }
}