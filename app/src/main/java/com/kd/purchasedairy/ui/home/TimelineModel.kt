package com.kd.purchasedairy.ui.home

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class TimelineModel(
    var timelineID: Int = 0,
    var price: Int = 0,
    var name: String = "",
    @ServerTimestamp var created: Timestamp = Timestamp.now(),
    var spec: String = "",
    var shop: String = ""
) {
}