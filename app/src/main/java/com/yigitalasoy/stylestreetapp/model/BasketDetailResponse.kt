package com.yigitalasoy.stylestreetapp.model

data class BasketDetailResponse(
    var basketDetailId: String? = "",
    var basketId: String? = "",
    var subProductId: String? = "",
    var productId: String? = "",
    var quantity: Int? = 0,
    var unitPrice: Int? = 0
)
