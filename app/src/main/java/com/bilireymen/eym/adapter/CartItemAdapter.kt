import android.content.Context
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.fragments.CartFragment
import com.bilireymen.eym.models.CartProduct
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class CartItemAdapter(private val context: Context,
                      val cartProducts: MutableList<CartProduct> = mutableListOf<CartProduct>(),
                      private val firebaseFirestore: FirebaseFirestore,
                      private val androidId: String,
                      private val fragment: Fragment) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    interface OnCartUpdateListener {
        fun onCartUpdated()
    }

    private var cartUpdateListener: OnCartUpdateListener? = null

    fun setOnCartUpdateListener(listener: OnCartUpdateListener) {
        cartUpdateListener = listener
    }

    interface OnFirebaseDeleteListener {
        fun onDeleteSuccess(position: Int)
        fun onDeleteFailure()
    }

    private var firebaseDeleteListener: OnFirebaseDeleteListener? = null

    fun setOnFirebaseDeleteListener(listener: OnFirebaseDeleteListener) {
        firebaseDeleteListener = listener
    }
    fun updateCartItem(cartProduct: CartProduct) {
        // Sepet öğesini güncelleme işlemleri burada yapılır
        val cartItemRef = firebaseFirestore.collection("Cart")
            .document(androidId)
            .collection("Cart Products")
            .document(cartProduct.product.id!!)

        val updateData = mapOf(
            "quantity" to cartProduct.quantity,
            "selectedSize" to cartProduct.selectedSize
        )

        // Belgeyi güncelle
        cartItemRef.update(updateData)
            .addOnSuccessListener {
                val updatedCartProducts = ArrayList(cartProducts)
                val index = updatedCartProducts.indexOfFirst { it.product.id == cartProduct.product.id }
                if (index != -1) {
                    updatedCartProducts[index] = cartProduct
                    cartProducts.clear()
                    cartProducts.addAll(updatedCartProducts)
                    notifyDataSetChanged()

                    cartUpdateListener?.onCartUpdated()
                }
            }
            .addOnFailureListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Unsuccessful")
                builder.setMessage("The product could not be updated.")
                builder.setPositiveButton("Ok") { dialog, which ->

                }
                builder.show()
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartProduct = cartProducts[position]

        Glide.with(holder.itemView.context).load(cartProduct.product.images!!.get(0))
            .into(holder.productImageImageView)
        holder.productNameTextView.text = cartProduct.product.name
        holder.productSizeTextView.text = cartProduct.selectedSize
        holder.productQuantityTextView.text = cartProduct.quantity.toString()
        holder.productPriceTextView.text = "$${cartProduct.product.price}"

        holder.decreaseButton.setOnClickListener {
            if (cartProduct.quantity!! > 1) {
                cartProduct.quantity = cartProduct.quantity!! - 1
                holder.productQuantityTextView.text = cartProduct.quantity.toString()
                updateCartItem(cartProduct)
            }
        }

        holder.increaseButton.setOnClickListener {
            cartProduct.quantity = cartProduct.quantity!! + 1
            holder.productQuantityTextView.text = cartProduct.quantity.toString()
            updateCartItem(cartProduct)

        }

        holder.removeButton.setOnClickListener {
            // Ürünü sepetten kaldırma işlemi
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val cartProduct = cartProducts[position]


                val builder = AlertDialog.Builder(context)
                builder.setTitle("Delete Product!")
                builder.setMessage("Are you sure you want to remove the product from the cart?")
                builder.setPositiveButton("Yes") { _, _ ->
                    // Firebase'den ürünü kaldır
                    val cartItemRef = firebaseFirestore.collection("Cart")
                        .document(androidId)
                        .collection("Cart Products")
                        .document(cartProduct.product.id!!)

                    cartItemRef.delete()
                    .addOnSuccessListener {
                        // Firebase'den başarıyla kaldırıldığında yerel listeyi güncelle
                        firebaseDeleteListener?.onDeleteSuccess(position)
                        // Sepeti güncellemek için updateCartItem işlevini çağırın
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Successful")
                        builder.setMessage("The product has been removed from the cart.")
                        builder.setPositiveButton("Ok") { dialog, which ->
                            // Kullanıcı Tamam'a tıkladığında yapılacak işlemler
                            cartProducts.removeAt(position)
                            notifyDataSetChanged()
                            cartUpdateListener?.onCartUpdated()
                        }
                        builder.show()
                    }
                    .addOnFailureListener {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Unsuccessful")
                        builder.setMessage("The product could not be removed.")
                        builder.setPositiveButton("Ok") { dialog, which ->
                            // Kullanıcı Tamam'a tıkladığında yapılacak işlemler
                            firebaseDeleteListener?.onDeleteFailure()
                        }
                        builder.show()
                    }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }


    }

    override fun getItemCount(): Int {
        return cartProducts.size
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageImageView: ImageView= itemView.findViewById(R.id.cartProductImage)
        val productNameTextView: TextView = itemView.findViewById(R.id.cartProductName)
        val productSizeTextView: TextView = itemView.findViewById(R.id.cartProductSize)
        val productQuantityTextView: TextView = itemView.findViewById(R.id.cartProductQ)
        val productPriceTextView: TextView = itemView.findViewById(R.id.cartProductPrice)
        val removeButton: TextView = itemView.findViewById(R.id.cartProductBin)
        val decreaseButton: TextView = itemView.findViewById(R.id.cartProductDecrease)
        val increaseButton: TextView = itemView.findViewById(R.id.cartProductIncrease)
    }
}