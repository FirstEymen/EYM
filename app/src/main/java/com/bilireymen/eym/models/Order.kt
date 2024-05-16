package com.bilireymen.eym.models

import java.io.Serializable
class Order:Serializable{
    var id:String?=null
    var checkout: Checkout?=null
    var time:Long?=null
    constructor(id:String?,checkout: Checkout,time:Long?){
        this.id=id
        this.checkout=checkout
        this.time=time
    }
    constructor(){}
}