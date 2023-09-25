package com.bilireymen.eym

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.mindrot.jbcrypt.BCrypt

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var edForgotEmail: TextInputEditText
    private lateinit var edNewPassword: TextInputEditText
    private lateinit var edPasswordConfirmation: TextInputEditText
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var newPasswordTextInputLayout: TextInputLayout
    private lateinit var passwordConfirmationTextInputLayout: TextInputLayout
    private lateinit var savePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edForgotEmail = findViewById(R.id.forgotEmail)
        edNewPassword = findViewById(R.id.forgotPassword)
        edPasswordConfirmation = findViewById(R.id.forgotPasswordConfirmation)
        emailTextInputLayout = findViewById(R.id.forgotEmailTextInputLayout)
        newPasswordTextInputLayout = findViewById(R.id.forgotPasswordTextInputLayout)
        passwordConfirmationTextInputLayout = findViewById(R.id.forgotPasswordConfirmationTextInputLayout)
        savePasswordButton = findViewById(R.id.savePassword)

        firestoreHelper = FirestoreHelper()

        savePasswordButton.setOnClickListener {
            val email = edForgotEmail.text.toString()
            val newPassword = edNewPassword.text.toString()
            val passwordConfirmation = edPasswordConfirmation.text.toString()

            if (isValidEmail(email) && isValidPassword(newPassword) && newPassword == passwordConfirmation) {
                // E-posta adresini kullanarak kullanıcıyı bulun
                firestoreHelper.findUserByEmail(email,
                    onSuccess = { user ->
                        if (user != null) {
                            // Yeni şifreyi güvenli bir şekilde saklayın (örneğin, BCrypt kullanarak)
                            val hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())

                            // Kullanıcının şifresini güncelleyin
                            firestoreHelper.updateUserPassword(
                                user.id!!, hashedPassword,
                                onSuccess = {
                                    // Şifre başarıyla güncellendiğini kullanıcıya bildirin
                                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                },
                                onFailure = { e ->
                                    Toast.makeText(this, "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(this, "User not found with this email", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Error finding user: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Geçerli e-posta ve şifre gereksinimlerini kontrol edin
                if (!isValidEmail(email)) {
                    emailTextInputLayout.error = "Invalid email"
                } else {
                    emailTextInputLayout.error = null
                }

                if (!isValidPassword(newPassword)) {
                    newPasswordTextInputLayout.error = "Invalid password"
                } else {
                    newPasswordTextInputLayout.error = null
                }

                if (newPassword != passwordConfirmation) {
                    passwordConfirmationTextInputLayout.error = "Passwords do not match"
                } else {
                    passwordConfirmationTextInputLayout.error = null
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}