package com.bilireymen.eym

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bilireymen.eym.adapter.SizesAdapter
import com.bilireymen.eym.adapter.ViewPager2Images
import com.bilireymen.eym.databinding.ActivityProductDetailsBinding
import com.bilireymen.eym.models.Product

class ProductDetailsActivity:AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var product = intent.getSerializableExtra("product") as? Product


        setupViewpager()
        setupSizesRv()


        binding.apply {
            productDetailsName.text = product?.name
            val oldPrice = "${"$" + product?.offerPercentage}"
            val spannable = SpannableString(oldPrice)
            spannable.setSpan(
                StrikethroughSpan(),
                0,
                oldPrice.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            productDetailsOldPrice.text = spannable
            val price = "${"$" + product?.price}"
            productDetailsPrice.text = price
            productDetailsPrice.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.red
                )
            )
            productDetailsDescription.text = product?.description
        }

        viewPagerAdapter.differ.submitList(product?.images)
        product?.sizes?.let { sizesAdapter.differ.submitList(it) }

    }

    private fun setupViewpager() {
        binding.viewPagerProductImages.adapter = viewPagerAdapter
    }

    private fun setupSizesRv() {
        binding.rvSize.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(
                this@ProductDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

}