package com.bilireymen.eym

import com.bilireymen.eym.models.User
import com.google.firebase.firestore.FirebaseFirestore
class FirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("Users")
    // Kullanıcı kaydını Firestore'a ekleyen fonksiyon
    fun registerUser(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Veri doğrulama işlemleri burada yapılabilir
        val newUserId = usersCollection.document().id
        // Firestore'a yeni bir kullanıcı ekleyin
        val newUser = User(
            id=newUserId,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            password = password
        )
        usersCollection.document(newUserId).set(newUser)
            .addOnSuccessListener {
                // Kullanıcı başarıyla kaydedildi
                onSuccess(newUserId)
            }
            .addOnFailureListener { e ->
                // Kullanıcı kaydı sırasında hata oluştu
                onFailure(e)
            }
    }
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val user = querySnapshot.documents[0].toObject(User::class.java)
                    onSuccess(user)
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun findUserByEmail(
        email: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val user = querySnapshot.documents[0].toObject(User::class.java)
                    onSuccess(user)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun updateUserPassword(
        userId: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Şifreyi güncellemek istediğiniz kullanıcının Firestore belgesini alın
        val userDocument = usersCollection.document(userId)
        userDocument.update("password", newPassword)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun updateUser(
        userId: String,
        updatedFields: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersCollection.document(userId)
            .update(updatedFields)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}