package com.bilireymen.eym.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bilireymen.eym.AddressListActivity
import com.bilireymen.eym.IntroductionActivity
import com.bilireymen.eym.R
import com.bilireymen.eym.Utils

class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var signOutButton: TextView
    private var selectedPosition: Int = -1 // Seçilen pozisyonu tutmak için ekledik

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        signOutButton = view.findViewById(R.id.signOutButton)

        val user = Utils.getUserFromSharedPreferences(requireContext())
        // Kullanıcı verilerini görüntüleme
        if (user != null) {
            tvUserName.text = user.firstName
            tvUserEmail.text = user.email
        }

        // Kullanıcının seçili adres pozisyonunu Firestore ve SharedPreferences'ten alın
        Utils.getUserSelectedAddressPositionFromDatabaseOrSharedPreferences(requireContext(), user?.id ?: "") { position ->
            selectedPosition = position
        }

        val myAddressProfile = view.findViewById<TextView>(R.id.myAddressProfile)
        myAddressProfile.setOnClickListener {
            val intent = Intent(requireContext(), AddressListActivity::class.java)
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