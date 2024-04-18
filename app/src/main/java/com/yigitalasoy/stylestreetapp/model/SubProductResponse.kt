package com.yigitalasoy.stylestreetapp.model

data class SubProductResponse(
    val subProductId: String?,
    val subProductSizeId: ProductSizeResponse?,
    val subProductStock: String?,
    val subProductName: String?,
    val subProductColorId: ProductColorResponse?,
    val subProductPrice: String?,
    val updateTime: String?,
    val subProductImageURL: ArrayList<String>?
)