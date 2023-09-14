package com.bilireymen.eym

import com.bilireymen.eym.models.User
import com.google.firebase.firestore.FirebaseFirestore

// Firestore veritabanına yeni kullanıcıyı eklemek için kullanılacak sınıf
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
            password = password // Kullanıcı parolasını ekleyin
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
    // Kullanıcı giriş işlemini kontrol eden fonksiyon
    fun loginUser(
        email: String,
        password: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Veri doğrulama işlemleri burada yapılabilir
        // Firestore'da kullanıcıyı e-posta adresine göre sorgula
        usersCollection.whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // E-posta ve şifreye sahip kullanıcı bulundu
                    // İlgili kullanıcının bilgilerini alabilirsiniz
                    val user = querySnapshot.documents[0].toObject(User::class.java)
                    onSuccess(user)
                } else {
                    // E-posta ve şifreye sahip kullanıcı bulunamadı
                    onFailure(Exception("Kullanıcı bulunamadı"))
                }
            }
            .addOnFailureListener { e ->
                // Sorgu sırasında hata oluştu
                onFailure(e)
            }
    }
}