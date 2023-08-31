package com.bilireymen.eym.models

import java.io.Serializable

 class Product:Serializable{

    var id:String?=null
    var name:String?=null
    var price: Double?=null
    var offerPercentage:Double?=null
    var description:String?=null
    var sizes:List<String>?=null
    var images:ArrayList<String>?=ArrayList()
     var category: Category?=null

     constructor(id: String?, name: String?,price: Double?,
                 offerPercentage:Double?,description:String?,sizes:List<String>?
     , images:ArrayList<String>?,category: Category?) {
         this.id = id
         this.name = name
         this.price=price
         this.offerPercentage=offerPercentage
         this.description=description
         this.sizes=sizes
         this.images=images
         this.category=category
     }

     constructor() {}



 }









