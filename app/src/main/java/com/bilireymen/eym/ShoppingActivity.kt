package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bilireymen.eym.databinding.ActivityShoppingBinding
import com.bilireymen.eym.fragments.CategoryFragment
import com.bilireymen.eym.fragments.HomeFragment
import com.bilireymen.eym.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
class ShoppingActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Kullanıcıyı al
        val user = Utils.getUserFromSharedPreferences(this)
        val app = EYMAplication.getInstance()
        app.user = user
        binding.bottomNavigation.setOnItemSelectedListener(this)
        binding.bottomNavigation.setSelectedItemId(R.id.homeFragment)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cartFragment -> {
                val intent = Intent(this@ShoppingActivity, CartActivity::class.java)
                startActivity(intent)
                return false
            }
            R.id.profileFragment -> {
                // Kullanıcı girişi kontrolü
                if (EYMAplication.getInstance().user != null) {
                    val profileFragment = ProfileFragment()
                    replaceFragment(profileFragment)
                    return true
                } else {
                    // Kullanıcı girişi yapmamış, giriş yapma ekranına yönlendir
                    val loginIntent = Intent(this@ShoppingActivity, IntroductionActivity::class.java)
                    startActivity(loginIntent)
                    return false
                }
            }
            R.id.homeFragment -> {
                val homeFragment = HomeFragment()
                replaceFragment(homeFragment)
                return true
            }
            R.id.categoryFragment -> {
                val categoryFragment = CategoryFragment()
                replaceFragment(categoryFragment)
                return true
            }
        }
        return false
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}