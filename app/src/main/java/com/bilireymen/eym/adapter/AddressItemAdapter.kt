package com.bilireymen.eym.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bilireymen.eym.R
import com.bilireymen.eym.Utils
import com.bilireymen.eym.models.Address

class AddressItemAdapter(private val context: Context, private val addressList: List<Address>) :
    RecyclerView.Adapter<AddressItemAdapter.AddressViewHolder>() {

    var selectedAddressPosition = -1
        private set

    // Seçilen adresi güncellemek için bu metod
    fun setSelectedAddressPosition(position: Int) {
        selectedAddressPosition = position
        notifyDataSetChanged()

        // Kullanıcının seçtiği adres pozisyonunu Firestore veya SharedPreferences'e kaydedin
        val userId = Utils.getUserFromSharedPreferences(context)?.id
        if (userId != null) {
            Utils.saveUserSelectedAddressPositionToDatabaseOrSharedPreferences(
                context,
                userId,
                selectedAddressPosition
            )
        }
    }

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressName: TextView = itemView.findViewById(R.id.addressName)
        val addressAddress: TextView = itemView.findViewById(R.id.addressAddress)
        val addressSelect: ImageView = itemView.findViewById(R.id.addressSelect)
        val cardView: CardView = itemView.findViewById(R.id.cartAddress)

        init {
            addressSelect.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    selectedAddressPosition = position
                    val selectedAddress = addressList[position]
                    selectedAddress.isSelected = true
                    notifyDataSetChanged()
                    setSelectedAddressPosition(selectedAddressPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val addressItem = addressList[position]

        holder.addressName.text = addressItem.name
        holder.addressAddress.text = addressItem.address

        if (position == selectedAddressPosition) {
            holder.addressSelect.setImageResource(R.drawable.address_select)
            addressItem.isSelected = true
        } else {
            holder.addressSelect.setImageResource(R.drawable.address_not_select)
            addressItem.isSelected = false
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }
}