package com.yigitalasoy.stylestreetapp.model

data class BasketResponse(
    var basketId: String?,
    var userId: String?,
    var basketProducts: ArrayList<BasketDetailResponse>?
)