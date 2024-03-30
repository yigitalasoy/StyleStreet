package com.yigitalasoy.stylestreetapp.model

data class SubProductResponse(
    val subProductId: String,
    val productSize: ProductSizeResponse,
    val stock: String,
    val productColor: ProductColorResponse,
    val price: String,
    val updateTime: String,
    val subProductImageURL: String
)