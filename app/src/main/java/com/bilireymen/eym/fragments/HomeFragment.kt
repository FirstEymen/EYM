package com.bilireymen.eym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilireymen.eym.R
import com.bilireymen.eym.ShoppingActivity
import com.bilireymen.eym.adapter.CarouselRvAdapter
import com.bilireymen.eym.adapter.GridRvAdapter
import com.bilireymen.eym.adapter.HorizontalRvItemAdapter
import com.bilireymen.eym.databinding.FragmentHomeBinding
import com.bilireymen.eym.eventbus.ProductListReceived
import com.bilireymen.eym.models.Product
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode




class HomeFragment:Fragment(R.layout.fragment_home) {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapterHorizontal: HorizontalRvItemAdapter
    private lateinit var adapterCarousel: CarouselRvAdapter
    private lateinit var adapterGrid: GridRvAdapter

    override fun onResume() {
        super.onResume()
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentHomeBinding.inflate(layoutInflater)



        horizontalRvAdapter()
        carouselRvAdapter()
        gridRvAdapter()


        return binding.root

    }

    private fun horizontalRvAdapter(){
        adapterHorizontal = HorizontalRvItemAdapter(requireContext(), (activity as ShoppingActivity).productArrayList ?: arrayListOf())
        binding.horizontalRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.horizontalRv.adapter = adapterHorizontal

        adapterHorizontal.setOnItemClickListener(object : HorizontalRvItemAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val action=HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(product)
                findNavController().navigate(action)
            }
        })
    }

    private fun carouselRvAdapter(){
        adapterCarousel = CarouselRvAdapter(requireContext(), (activity as ShoppingActivity).productArrayList ?: arrayListOf())
        binding.carouselRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
       binding.carouselRv.adapter=adapterCarousel
       binding.carouselRv.apply {
           set3DItem(true)
           setAlpha(true)
           setInfinite(true)
       }
        adapterCarousel.setOnItemClickListener(object : CarouselRvAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(product)
                findNavController().navigate(action)
            }
        })
    }

    private fun gridRvAdapter(){
        adapterGrid = GridRvAdapter(requireContext(), (activity as ShoppingActivity).productArrayList ?: arrayListOf())
        binding.gridRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.gridRV.adapter = adapterGrid

        adapterGrid.setOnItemClickListener(object : GridRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {

                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailsFragment(product)
                findNavController().navigate(action)
            }
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ProductListReceived?) {
        horizontalRvAdapter()
        carouselRvAdapter()
        gridRvAdapter()
    }



}