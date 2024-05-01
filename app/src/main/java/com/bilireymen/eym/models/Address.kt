package com.bilireymen.eym.models


import java.io.Serializable

 class Address:Serializable{
     var isDefaultAdress: Boolean = false
     var isSelected: Boolean = false
     var name: String? = null
     var address: String? = null

     constructor(isDefaultAddress: Boolean,isSelected:Boolean,name: String,address: String){
         this.isDefaultAdress=isDefaultAddress
         this.isSelected=isSelected
         this.name=name
         this.address=address
     }
     constructor(){}
     constructor(addressName: String, addressText: String){
         this.name=addressName
         this.address=addressText
     }
 }