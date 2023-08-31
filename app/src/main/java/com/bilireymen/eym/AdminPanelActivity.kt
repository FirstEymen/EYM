package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.databinding.ActivityAdminPanelBinding

class AdminPanelActivity:AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.productAdd.setOnClickListener{
            val intent=Intent(this,AdminProductAdd::class.java)
            startActivity(intent)
        }

        binding.categoryAdd.setOnClickListener{
            val intent=Intent(this,AdminCategoryAddActivity::class.java)
            startActivity(intent)
        }

        binding.productEdit.setOnClickListener{
            val intent=Intent(this,AdminMproductListActivity::class.java)
            startActivity(intent)
        }

        binding.categoryEdit.setOnClickListener{
            val intent=Intent(this,AdminCategoryListActivity::class.java)
            startActivity(intent)
        }

    }





}