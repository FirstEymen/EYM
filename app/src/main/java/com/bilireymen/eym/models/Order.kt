package com.bilireymen.eym.models

import java.io.Serializable

class Order:Serializable{
    var id:String?=null
    var checkout: Checkout?=null

    constructor(id:String?,checkout: Checkout){
        this.id=id
        this.checkout=checkout
    }

    constructor(){}

}