package com.bilireymen.eym.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilireymen.eym.R
import com.bilireymen.eym.adapter.ViewPager2Images
import com.bilireymen.eym.databinding.FragmentProductDetailsBinding
import com.bilireymen.eym.adapter.SizesAdapter

class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val  viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupViewpager()

        binding.apply {
            productDetailsName.text=product.name
            val oldPrice = "${"$" + product.offerPercentage}"
            val spannable = SpannableString(oldPrice)
            spannable.setSpan(StrikethroughSpan(), 0, oldPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            productDetailsOldPrice.text = spannable
            val price = "${"$" + product.price}"
            productDetailsPrice.text = price
            productDetailsPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            productDetailsDescription.text=product.description
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter=viewPagerAdapter
        }
    }

    private fun setupSizesRv() {
        binding.rvSize.apply {
            adapter=sizesAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}