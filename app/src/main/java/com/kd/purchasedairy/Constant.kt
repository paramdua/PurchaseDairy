package com.kd.purchasedairy

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constant {
    const val PRODUCT = "Product"
    const val SHOP = "Shop"
    const val TIMELINE = "Timeline"
    const val PRODUCT_NAME = "name"
    const val PRODUCT_PRICE = "price"
    const val PRODUCT_ID = "ProductId"
    const val SHOP_NAME = "name"
    const val SHOP_ADDRESS = "address"
    const val SHOP_ID = "ShopId"

    fun getShopRef():CollectionReference{
        return Firebase.firestore.collection("Shop")
    }

    fun getProdRef(): CollectionReference {
        return Firebase.firestore.collection("Product")
    }
}