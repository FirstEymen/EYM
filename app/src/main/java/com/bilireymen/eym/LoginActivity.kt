package com.bilireymen.eym

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var edEmailLogin: TextInputEditText
    private lateinit var edPasswordLogin: TextInputEditText
    private lateinit var emailTextInputLayout:TextInputLayout
    private lateinit var passwordTextInputLayout:TextInputLayout
    private lateinit var buttonLogin: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edEmailLogin = findViewById(R.id.edEmailLogin)
        edPasswordLogin = findViewById(R.id.edPasswordLogin)
        emailTextInputLayout=findViewById(R.id.emailTextInputLayout)
        passwordTextInputLayout=findViewById(R.id.passwordTextInputLayout)
        buttonLogin = findViewById(R.id.buttonLogin)

        firestoreHelper = FirestoreHelper()
        firestore=FirebaseFirestore.getInstance()

        buttonLogin.setOnClickListener {
            val email = edEmailLogin.text.toString()
            val password = edPasswordLogin.text.toString()

            if (isValidEmail(email) && isValidPassword(password)) {

                clearErrors()

                firestoreHelper.loginUser(email, password,
                    onSuccess = { user ->
                        if (user != null) {
                            val hashedPassword = BCrypt.hashpw(password, user.password) // Firestore'dan alınan hash ile karşılaştır
                            if (hashedPassword == user.password) {
                                // Parola doğru, giriş yapabilirsiniz
                                Utils.saveUserDataToSharedPreferences(this@LoginActivity, user)
                                val app = EYMAplication.getInstance()
                                app.user = user

                                val intent = Intent(this@LoginActivity, ShoppingActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                showPasswordError("Invalid email or password")
                            }
                        } else {
                            showPasswordError("Invalid email or password")
                        }
                    },
                    onFailure = { e ->
                        showEmailError("Error logging in: ${e.message}")
                    }
                )
            } else {
                if (!isValidEmail(email)) {
                    showEmailError("Invalid email")
                }
                if (password.isEmpty()) {
                    showPasswordError("Password cannot be empty")
                }
            }
        }

        val forgotPasswordLink = findViewById<TextView>(R.id.forgotPasswordLink)
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }


    private fun showEmailError(errorMessage: String) {
        emailTextInputLayout.error = errorMessage
    }

    private fun showPasswordError(errorMessage: String) {
        passwordTextInputLayout.error = errorMessage
    }

    private fun clearErrors() {
        emailTextInputLayout.error = null
        passwordTextInputLayout.error = null
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 6
    }

}