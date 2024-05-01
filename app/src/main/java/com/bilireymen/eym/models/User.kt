package com.bilireymen.eym.models

data class User(
    val id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val password: String? = null,
    val addresses: MutableList<Address>? = mutableListOf()
) {

}












