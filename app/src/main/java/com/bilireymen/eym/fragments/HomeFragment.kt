package com.bilireymen.eym.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilireymen.eym.EYMAplication
import com.bilireymen.eym.ProductDetailsActivity
import com.bilireymen.eym.R
import com.bilireymen.eym.adapter.CarouselRvAdapter
import com.bilireymen.eym.adapter.GridRvAdapter
import com.bilireymen.eym.adapter.HorizontalRvItemAdapter
import com.bilireymen.eym.databinding.FragmentHomeBinding
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment:Fragment(R.layout.fragment_home) {
    private lateinit var firestore: FirebaseFirestore
    var productArrayList:ArrayList<Product> = ArrayList()
    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapterHorizontal: HorizontalRvItemAdapter
    private lateinit var adapterCarousel: CarouselRvAdapter
    private lateinit var adapterGrid: GridRvAdapter


    override fun onResume() {
        super.onResume()
    }
    override fun onStart() {
        super.onStart()

    }
    override fun onStop() {
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        firestore = FirebaseFirestore.getInstance()

        getData()
        horizontalRvAdapter()
        carouselRvAdapter()
        gridRvAdapter()

        return binding.root
    }


    private fun horizontalRvAdapter(){
        adapterHorizontal = HorizontalRvItemAdapter(requireContext(), productArrayList ?: arrayListOf())
        binding.horizontalRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.horizontalRv.adapter = adapterHorizontal

        adapterHorizontal.setOnItemClickListener(object : HorizontalRvItemAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })

    }

    private fun carouselRvAdapter(){
        adapterCarousel = CarouselRvAdapter(requireContext(), productArrayList ?: arrayListOf())
        binding.carouselRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
       binding.carouselRv.adapter=adapterCarousel
       binding.carouselRv.apply {
           set3DItem(true)
           setAlpha(true)
           setInfinite(true)
       }
        adapterCarousel.setOnItemClickListener(object : CarouselRvAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, product: Product) {
                val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })
    }

    private fun gridRvAdapter(){
        adapterGrid = GridRvAdapter(requireContext(), productArrayList ?: arrayListOf())
        binding.gridRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.gridRV.adapter = adapterGrid

        adapterGrid.setOnItemClickListener(object : GridRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, product: Product) {

                val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })

    }

    private fun getData(){
        firestore.collection("Products")
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    val data: Map<String, Any> = document.data
                    val id = data["id"] as String?
                    val name = data["name"] as String
                    val price = data["price"] as Double
                    val offerPercentage = data["offerPercentage"] as Double
                    val description = data["description"] as String
                    val sizes = data["sizes"] as List<String>
                    val images = data["images"] as ArrayList<String>
                    val category: Category?=null
                    var arrayList=ArrayList<String>()
                    arrayList.addAll(images)
                    val product = Product(id, name, price, offerPercentage, description, sizes, arrayList,category)
                    productArrayList.add(product)
                }
                binding.horizontalRv.adapter!!.notifyDataSetChanged()
                binding.carouselRv.adapter!!.notifyDataSetChanged()
                binding.gridRV.adapter!!.notifyDataSetChanged()

            }
            .addOnFailureListener {
                val builder =
                    AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("An error occurred")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                       EYMAplication.appContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
            }
    }

}