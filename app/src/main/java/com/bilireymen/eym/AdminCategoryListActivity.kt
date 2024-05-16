package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bilireymen.eym.adapter.CategoryAdapter
import com.bilireymen.eym.databinding.ActivityAdminCategoryListBinding
import com.bilireymen.eym.models.Category
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
class AdminCategoryListActivity:AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding:ActivityAdminCategoryListBinding
    lateinit var categoryArrayList: ArrayList<Category>
    lateinit var categoryAdapter: CategoryAdapter
    var from:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCategoryListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        categoryArrayList = ArrayList()
        from=intent.getStringExtra("from")
        val uuid = UUID.randomUUID()
        val imageName = "images/$uuid.jpg"
        firestore=FirebaseFirestore.getInstance()
        binding.categoryListRecyclerView.layoutManager = GridLayoutManager(this,2)
        categoryAdapter = CategoryAdapter(categoryArrayList,this,from)
        binding.categoryListRecyclerView.adapter = categoryAdapter
        getData()
    }
    private fun getData(){
        firestore.collection("Categorys")
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    val data: Map<String, Any> = document.data
                    val id = data["id"] as String?
                    val name = data["name"] as String
                    val images = data["images"] as ArrayList<String>
                    var arrayList=ArrayList<String>()
                    arrayList.addAll(images)
                    val category = Category(id, name,arrayList)
                    categoryArrayList.add(category)
                }
                categoryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                val builder =
                    AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("An error occurred")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
            }
    }
}
