package com.bilireymen.eym.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bilireymen.eym.AddressListActivity
import com.bilireymen.eym.EYMAplication
import com.bilireymen.eym.FavoritesActivity
import com.bilireymen.eym.IntroductionActivity
import com.bilireymen.eym.OrderActivity
import com.bilireymen.eym.ProfileActivity
import com.bilireymen.eym.R
import com.bilireymen.eym.ShoppingActivity
import com.bilireymen.eym.Utils
import com.bilireymen.eym.models.Order
class ProfileFragment : Fragment() {
    private lateinit var tvUserEmail: TextView
    private lateinit var signOutButton: TextView
    private var selectedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        signOutButton = view.findViewById(R.id.signOutButton)
        val user = Utils.getUserFromSharedPreferences(requireContext())
        if (EYMAplication.getInstance().user!=null) {
            tvUserEmail.text = user!!.email
        }else{
            startActivity(Intent(requireContext(), IntroductionActivity::class.java))
            activity?.finish()
            return
        }
        Utils.getUserSelectedAddressPositionFromDatabaseOrSharedPreferences(requireContext(), user?.id ?: "") { position ->
            selectedPosition = position
        }
        val myAddressProfile = view.findViewById<TextView>(R.id.myAddressProfile)
        myAddressProfile.setOnClickListener {
            val intent = Intent(requireContext(), AddressListActivity::class.java)
            intent.putExtra("fromProfileFragment", true)
            startActivity(intent)
        }
        val myOrdersProfile=view.findViewById<TextView>(R.id.myOrdersProfile)
        myOrdersProfile.setOnClickListener{
            val intent=Intent(requireContext(),OrderActivity::class.java)
            startActivity(intent)
        }
        val myProfileProfile=view.findViewById<TextView>(R.id.myProfileProfile)
        myProfileProfile.setOnClickListener{
            val intent=Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        val myFavoritesProfile=view.findViewById<TextView>(R.id.myFavoritesProfile)
        myFavoritesProfile.setOnClickListener{
            val intent=Intent(requireContext(), FavoritesActivity::class.java)
            startActivity(intent)
        }
        signOutButton.setOnClickListener {
            Utils.clearUserDataFromSharedPreferences(requireContext())
            Utils.saveUserSelectedAddressPositionToDatabaseOrSharedPreferences(requireContext(), user?.id ?: "", selectedPosition)
            val intent = Intent(requireContext(), IntroductionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}