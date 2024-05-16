package com.bilireymen.eym.models

import java.io.Serializable
class CartProduct:Serializable{
    lateinit var product: Product
    var quantity: Int?=null
    var selectedSize: String?=null
    constructor(product: Product,quantity:Int,selectedSize:String){
        this.product=product
        this.quantity=quantity
        this.selectedSize=selectedSize
    }
    constructor(){}
}
