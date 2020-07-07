package com.kd.purchasedairy.ui.shop

data class ShopModel(
    var shopId: Int = 0,
    var phn: String = "",
    var name: String = "",
    var address: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0
) {
}