package com.bilireymen.eym

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.fragments.CartFragment
class CartActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_activity)
        val fragment = CartFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onBackPressed() {
      this@CartActivity.finish()
    }
}