package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bilireymen.eym.databinding.ActivityAdminRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class AdminRegisterActivity:AppCompatActivity(){

        private lateinit var binding:ActivityAdminRegisterBinding
        private lateinit var firebaseAuth:FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)

            binding= ActivityAdminRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)

            firebaseAuth=FirebaseAuth.getInstance()

            binding.textView.setOnClickListener{
                val intent=Intent(this,AdminLoginActivity::class.java)
                startActivity(intent)
            }

            binding.adminSignUpButton.setOnClickListener{
                val email=binding.adminEmail.text.toString()
                val pass=binding.adminPassword.text.toString()
                val confirmPass=binding.adminConfirmPassword.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                    if (pass == confirmPass){

                        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                            if (it.isSuccessful){
                                val intent=Intent(this,AdminLoginActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
                        builder.setTitle("Alert!!!")
                        builder.setMessage("Password is not matching")
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            Toast.makeText(applicationContext,
                                android.R.string.yes, Toast.LENGTH_SHORT).show()
                        }
                        builder.show()
                    }
                }else{
                    val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
                    builder.setTitle("Alert!!!")
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