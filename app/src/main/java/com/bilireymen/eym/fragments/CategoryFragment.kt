package com.bilireymen.eym.fragments

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bilireymen.eym.CategoryProductsActivity
import com.bilireymen.eym.EYMAplication
import com.bilireymen.eym.R
import com.bilireymen.eym.adapter.GridRvCategoryAdapter
import com.bilireymen.eym.databinding.FragmentCategoryBinding
import com.bilireymen.eym.models.Category
import com.google.firebase.firestore.FirebaseFirestore
class CategoryFragment:Fragment(R.layout.fragment_category) {
    private lateinit var firestore: FirebaseFirestore
    var categoryArrayList:ArrayList<Category> = ArrayList()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapterGridCategory:GridRvCategoryAdapter
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
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        firestore = FirebaseFirestore.getInstance()
        getData()
        gridRvCategoryAdapter()
        return binding.root
    }
    private fun gridRvCategoryAdapter(){
        adapterGridCategory = GridRvCategoryAdapter(requireContext(), categoryArrayList ?: arrayListOf())
        binding.gridRvCategoryAdapter.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.gridRvCategoryAdapter.adapter = adapterGridCategory

        adapterGridCategory.setOnItemClickListener(object: GridRvCategoryAdapter.OnItemClickListener{
            override fun onItemClick(position:Int , category: Category){
                val intent = Intent(requireContext(), CategoryProductsActivity::class.java)
                intent.putExtra("category", category)
                startActivity(intent)
            }
        })
    }
    private fun getData(){
        firestore.collection("Categorys")
            .get()
            .addOnSuccessListener { result->
                for(document in result){
                    val data: Map<String, Any> = document.data
                    val id = data["id"] as String?
                    val name = data["name"] as String
                    val images = data["images"] as ArrayList<String>
                    var arrayList=ArrayList<String>()
                    arrayList.addAll(images)
                    val category = Category(id,name,arrayList)
                    categoryArrayList.add(category)
                }
                binding.gridRvCategoryAdapter.adapter!!.notifyDataSetChanged()
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