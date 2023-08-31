package com.bilireymen.eym

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bilireymen.eym.databinding.ActivityAdminCategoryAddBinding
import com.bilireymen.eym.models.Category
import com.bilireymen.eym.models.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AdminCategoryAddActivity:AppCompatActivity() {

    private val binding by lazy {ActivityAdminCategoryAddBinding.inflate(layoutInflater)}
    private var selectedImages= mutableListOf<Uri>()
    private val categorysStorage=Firebase.storage.reference
    private val firestore=Firebase.firestore
    private var category: Category? = null


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        category = intent.getSerializableExtra("category") as Category?

        if (category != null) {
            initEditCategory()
        }else category= Category()

        val selectedImagesActivityResult=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    result->
            if (result.resultCode== RESULT_OK){
                val intent=result.data

                if (intent?.clipData!=null){
                    val count=intent.clipData?.itemCount?:0
                    (0 until count).forEach{
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                }else{
                    val imageUri=intent?.data
                    imageUri?.let{selectedImages.add(it)}
                }
                updateImages()
            }
        }

        binding.buttonCategoryImages.setOnClickListener{
            val intent= Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type="image/*"
            selectedImagesActivityResult.launch(intent)
        }


    }

    private fun initEditCategory(){
        binding.categoryName.setText(category!!.name)
    }

    private fun updateImages() {
        binding.tvCategoryImages.text=selectedImages.size.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.saveCategory){
            val productValidation=validateInformation()
            if (!productValidation){
                val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("Check your inputs")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT).show()
                }
                builder.show()
                return false
            }
            saveCategory()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveCategory(){

        category!!.name=binding.categoryName.text.toString().trim()
        val imagesByteArrays=getImagesByteArrays()

        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showLoading()
            }

            try {
                async {
                    imagesByteArrays.forEach{
                        val id= UUID.randomUUID().toString()
                        launch {
                            val imageStorage=categorysStorage.child("categorys/images/$id")
                            val result=imageStorage.putBytes(it).await()
                            val downloadUrl=result.storage.downloadUrl.await().toString()
                            category!!.images!!.add(downloadUrl)
                        }
                    }
                }.await()

            }catch (e:java.lang.Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    showLoading()
                }
            }

            val id=if(category!=null&&category!!.id!=null){category!!.id}else
                UUID.randomUUID().toString()
                 category!!.id=id



            firestore.collection("Categorys").document(id!!).set(category!!).addOnSuccessListener {
                hideLoading()
            }.addOnFailureListener{
                hideLoading()
                Log.e("Error",it.message.toString())
            }
        }
    }

    private fun hideLoading() {
        binding.categoryProgressbar.visibility= View.INVISIBLE
        val builder =
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle("Notification")
        builder.setMessage("Uploaded")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    private fun showLoading() {
        binding.categoryProgressbar.visibility=View.VISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {

        val imagesByteArray= mutableListOf<ByteArray>()
        selectedImages.forEach{
            val stream= ByteArrayOutputStream()
            val imageBmp= MediaStore.Images.Media.getBitmap(contentResolver,it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG,100,stream)){
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    private fun validateInformation(): Boolean {

        if (binding.categoryName.text.toString().trim().isEmpty())
            return false

        if (selectedImages.isEmpty()&&category!!.images!!.isEmpty())
            return false

        return true
    }
}