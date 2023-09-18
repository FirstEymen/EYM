package com.bilireymen.eym.models

data class Address(
    var isDefaultAdress: Boolean = false,
    var isSelected: Boolean = false,
    val name: String? = null,
    val address: String? = null
)