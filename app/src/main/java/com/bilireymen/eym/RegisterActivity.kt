package com.bilireymen.eym

import android.os.Bundle
import android.util.Patterns
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.mindrot.jbcrypt.BCrypt
class RegisterActivity : AppCompatActivity() {
    private val firestoreHelper = FirestoreHelper()
    private lateinit var registerName: TextInputEditText
    private lateinit var registerLastName: TextInputEditText
    private lateinit var edPhoneRegister: TextInputEditText
    private lateinit var edEmailRegister: TextInputEditText
    private lateinit var edPasswordRegister: TextInputEditText
    private lateinit var nameTextInputLayout: TextInputLayout
    private lateinit var lastNameTextInputLayout: TextInputLayout
    private lateinit var phoneTextInputLayout: TextInputLayout
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var buttonRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerName = findViewById(R.id.registerName)
        registerLastName = findViewById(R.id.registerLastName)
        edPhoneRegister = findViewById(R.id.edPhoneRegister)
        edEmailRegister = findViewById(R.id.edEmailRegister)
        edPasswordRegister = findViewById(R.id.edPasswordRegister)
        buttonRegister = findViewById(R.id.buttonRegister)
        nameTextInputLayout=findViewById(R.id.nameTextInputLayout)
        lastNameTextInputLayout=findViewById(R.id.lastNameTextInputLayout)
        phoneTextInputLayout=findViewById(R.id.phoneTextInputLayout)
        emailTextInputLayout=findViewById(R.id.emailTextInputLayout)
        passwordTextInputLayout=findViewById(R.id.passwordTextInputLayout)
        // Register düğmesine tıklanınca çalışacak işlev
        buttonRegister.setOnClickListener {
            val firstName = registerName.text.toString()
            val lastName = registerLastName.text.toString()
            val phone = edPhoneRegister.text.toString()
            val email = edEmailRegister.text.toString()
            val password = edPasswordRegister.text.toString()
            if (validateInputs(firstName, lastName, phone, email, password)) {
                // Hataları temizle
                nameTextInputLayout.error = null
                lastNameTextInputLayout.error = null
                phoneTextInputLayout.error = null
                emailTextInputLayout.error = null
                passwordTextInputLayout.error = null
                val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
                // Veri doğrulama işlemlerini geçtiğinizden emin olduktan sonra Firestore'a kaydı yapabilirsiniz.
                firestoreHelper.registerUser(
                    firstName,
                    lastName,
                    phone,
                    email,
                    hashedPassword,
                    { userId ->
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
                    },
                    { e ->
                        val builder =
                            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        builder.setTitle("Alert!!")
                        builder.setMessage("Failed")
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                android.R.string.yes, Toast.LENGTH_SHORT
                            ).show()
                        }
                        builder.show()
                    }
                )
            }
        }
    }
    private fun validateInputs(firstName: String, lastName: String, phone: String, email: String, password: String): Boolean {
        var isValid = true
        if (firstName.isEmpty()) {
            nameTextInputLayout.error = "Namespace cannot be empty"
            isValid = false
        } else {
            nameTextInputLayout.error = null
        }
        if (lastName.isEmpty()) {
            lastNameTextInputLayout.error = "Surname field cannot be empty"
            isValid = false
        } else {
            lastNameTextInputLayout.error = null
        }
        if (isValidPhoneNumber(phone)) {
            phoneTextInputLayout.error = null
        } else {
            phoneTextInputLayout.error = "Invalid phone number"
            isValid = false
        }
        if (email.isEmpty()) {
            emailTextInputLayout.error = "Email field cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.error = "Invalid email format"
            isValid = false
        } else {
            emailTextInputLayout.error = null
        }
        if (password.isEmpty()) {
            passwordTextInputLayout.error = "Password field cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            passwordTextInputLayout.error = "Password must contain at least 6 characters"
            isValid = false
        } else {
            passwordTextInputLayout.error = null
        }
        return isValid
    }
    private fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = Regex("^(\\+\\d{1,3}[- ]?)?\\d{10}\$")
        return phonePattern.matches(phone)
    }
}