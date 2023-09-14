package com.bilireymen.eym

import android.app.Application
import android.content.Context
import android.content.SharedPreferences




class CommonFunctions {

    companion object{
        fun getFavoriteProductList():HashSet<String>{

            val mPrefs =  EYMAplication.appContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
           return mPrefs.getStringSet("favoriteProducts",HashSet<String>()) as HashSet<String>
        }
        fun setFavoriteProductList(productId:String){
            val mPrefs =  EYMAplication.appContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
            var favoriteProductList= getFavoriteProductList()
            if (!favoriteProductList.contains(productId))
            favoriteProductList.add(productId)
            else
                favoriteProductList.remove(productId)
            mPrefs.edit().remove("favoriteProducts").commit()
            mPrefs.edit().putStringSet("favoriteProducts",favoriteProductList).commit()
        }
    }
}