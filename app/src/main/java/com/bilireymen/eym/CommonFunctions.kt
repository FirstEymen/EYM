package com.bilireymen.eym

import android.content.Context
class CommonFunctions {
    companion object{
        fun getFavoriteProductList():HashSet<String>{
            val mPrefs =  EYMAplication.appContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
           return mPrefs.getStringSet("favoriteProducts",HashSet<String>()) as HashSet<String>
        }
        fun setFavoriteProductList(productId:String){
            val mPrefs = EYMAplication.appContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
            var favoriteProductList = getFavoriteProductList().toMutableSet()
            if (favoriteProductList.contains(productId))
                favoriteProductList.remove(productId)
            else
                favoriteProductList.add(productId)
            mPrefs.edit().putStringSet("favoriteProducts", favoriteProductList).apply()
        }
    }
}