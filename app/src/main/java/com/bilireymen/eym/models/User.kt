package com.bilireymen.eym.models

data class User(
    val id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    val password: String? = null,
    val addresses: MutableList<Address>? = mutableListOf()
) {
}












