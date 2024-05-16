package com.bilireymen.eym

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.databinding.ActivityAdminLoginBinding
import com.bilireymen.eym.databinding.ActivityAdminRegisterBinding
import com.google.firebase.auth.FirebaseAuth
@Suppress("DEPRECATION")
class AdminLoginActivity:AppCompatActivity(){
    private lateinit var binding:ActivityAdminLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding=ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser!=null){
            val intent = Intent(this, AdminPanelActivity::class.java)
            startActivity(intent)
        }
        binding.textView.setOnClickListener{
            val intent=Intent(this,AdminRegisterActivity::class.java)
            startActivity(intent)
        }
        binding.adminSignInButton.setOnClickListener{
            val email=binding.adminEmail.text.toString()
            val pass=binding.adminPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, AdminPanelActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    }else{
                val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
                builder.setTitle("Alert!!")
                builder.setMessage("Empty Fields Are not Allowed!!")
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Toast.makeText(applicationContext,
                        android.R.string.yes, Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }
    }
}