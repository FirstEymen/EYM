package com.bilireymen.eym.models

import java.io.Serializable
 class Category:Serializable {
    var id:String?=null
    var name:String?=null
    var images: ArrayList<String>? = ArrayList()
    constructor(id:String?,name:String?,images:ArrayList<String>?){
        this.id = id
        this.name = name
        this.images=images
    }
    constructor() {}
     companion object{
         fun categoryHashMapToCategory(hashMap:HashMap<String,Any>):Category{
             var category:Category= Category()
             category.id=hashMap["id"] as String
             category.name=hashMap["name"] as String
             category.images=hashMap["images"] as ArrayList<String>
             return category
         }
     }
}
