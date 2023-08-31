package com.bilireymen.eym

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bilireymen.eym.adapter.ProductAdapter
import com.bilireymen.eym.databinding.ActivityAdminProductAddBinding
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

@Suppress("DEPRECATION")
class AdminProductAdd : AppCompatActivity() {
    companion object {
        const val CATEGORY_SELECTION_REQUEST_CODE = 1001
        const val  PRODUCT_LIST_REQUEST_CODE = 1002
    }



    private val binding by lazy { ActivityAdminProductAddBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private val productsStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore
    private var product: Product? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        product = intent.getSerializableExtra("product") as Product?

        if (product != null) {
            initEditProduct()
        } else product = Product()

        val selectedImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updateImages()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectedImagesActivityResult.launch(intent)
        }

        binding.selectCategoryButton.setOnClickListener {
            val intent = Intent(this, AdminCategoryListActivity::class.java)
            intent.putExtra("from", "product")
            startActivityForResult(intent, CATEGORY_SELECTION_REQUEST_CODE)
        }

        binding.deleteProduct.setOnClickListener {
            val productId = product?.id

            if (productId != null) {
                showDeleteConfirmationDialog(productId)
            } else {
                Toast.makeText(this, "Product ID is missing.", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun showDeleteConfirmationDialog(productId: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle("Delete Product")
        builder.setMessage("Are you sure you want to delete this product?")
        builder.setPositiveButton("Delete") { dialog, which ->
            deleteProduct(productId)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteProduct(productId: String) {
        firestore.collection("Products").document(productId)
            .delete()
            .addOnSuccessListener {
                val builder =
                    AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("Succes")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()

                val productListIntent = Intent(this, AdminMproductListActivity::class.java)
                startActivityForResult(productListIntent, PRODUCT_LIST_REQUEST_CODE)
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

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CATEGORY_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedCategory = data?.getSerializableExtra("category") as Category?
            if (selectedCategory != null) {
                product!!.category = selectedCategory
                updateSelectedCategoryView(selectedCategory.name)
            }
        }
    }

    private fun updateSelectedCategoryView(categoryName: String?) {
        binding.selectedCategoryTextView.text = categoryName
    }

    private fun initEditProduct() {
        binding.edName.setText(product!!.name)
        binding.edPrice.setText(product!!.price.toString())
        binding.offerPercentage.setText(product!!.offerPercentage.toString())
        binding.edDescription.setText(product!!.description)
        binding.edSizes.setText(product!!.sizes.toString())
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                val builder = AlertDialog.Builder(
                    ContextThemeWrapper(
                        this,
                        R.style.AlertDialogCustom
                    )
                )
                builder.setTitle("Alert!!")
                builder.setMessage("Check your inputs")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT
                    ).show()
                }
                builder.show()
                return false
            }
            saveProduct()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        product!!.name = binding.edName.text.toString().trim()
        product!!.price = binding.edPrice.text.toString().trim().toDouble()
        product!!.offerPercentage = binding.offerPercentage.text.toString().trim().toDouble()
        product!!.description = binding.edDescription.text.toString().trim()
        product!!.sizes = getSizeList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays()
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }

            try {
                async {
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage =
                                productsStorage.child("products/images/$id")
                            val result = imageStorage.putBytes(it).await()
                            val downloadUrl =
                                result.storage.downloadUrl.await().toString()
                            product!!.images!!.add(downloadUrl)
                        }
                    }
                }.await()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showLoading()
                }
            }
            val id = if (product != null && product!!.id != null) {
                product!!.id
            } else
                UUID.randomUUID().toString()
            product!!.id = id
            firestore.collection("Products").document(id!!).set(product!!)
                .addOnSuccessListener {
                    hideLoading()
                }.addOnFailureListener {
                    hideLoading()
                    Log.e("Error", it.message.toString())
                }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
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
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 50, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    private fun getSizeList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty())
            return null
        val sizesList = sizesStr.split(",")
        return sizesList
    }

    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false

        if (binding.edName.text.toString().trim().isEmpty())
            return false

        if (selectedImages.isEmpty() && product!!.images!!.isEmpty())
            return false

        return true
    }


}
