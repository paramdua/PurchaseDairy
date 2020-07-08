package com.kd.purchasedairy

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Constant {
    const val PRODUCT = "Product"
    const val SHOP = "Shop"
    const val TIMELINE = "Timeline"
    const val PRODUCT_NAME = "name"
    const val PRODUCT_SPEC = "specification"
    const val PRODUCT_PRICE = "price"
    const val PRODUCT_ID = "ProductId"
    const val TIMELINE_ID = "TimelineID"
    const val SHOP_NAME = "name"
    const val SHOP_ADDRESS = "address"
    const val SHOP_LAT = "lat"
    const val SHOP_LNG = "lng"
    const val SHOP_ID = "ShopId"
    const val SHOP_PHN = "phn"

    fun getShopRef():CollectionReference{
        return Firebase.firestore.collection("Shop")
    }

    fun getTimelineRef(): CollectionReference {
        return Firebase.firestore.collection("TimeLine")
    }

    fun getProdRef(): CollectionReference {
        return Firebase.firestore.collection("Product")
    }
}