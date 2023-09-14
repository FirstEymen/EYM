package com.bilireymen.eym.models

class CartProduct(
    val product: Product,
    var quantity: Int?=null,
    val selectedSize: String?=null

){
    constructor() : this(Product(),1,null)
}
