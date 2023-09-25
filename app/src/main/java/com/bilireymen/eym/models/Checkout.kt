package com.bilireymen.eym.models


import java.io.Serializable

class Checkout:Serializable{
    var cartProduct: ArrayList<CartProduct>?=null
    var address: Address?=null
    var userId:String?=null

    constructor(cartProduct: ArrayList<CartProduct>,address: Address,userId: String){
        this.cartProduct=cartProduct
        this.address=address
        this.userId=userId
    }
    constructor(){}
}
