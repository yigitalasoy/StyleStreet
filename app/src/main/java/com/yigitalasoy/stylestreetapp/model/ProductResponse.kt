package com.yigitalasoy.stylestreetapp.model

data class ProductResponse(
    var productId: String?,
    var productName: String?,
    var categoryId: CategoryResponse?,
    var allProducts: ArrayList<SubProductResponse>?
) {
    private val _allProducts: MutableList<SubProductResponse> = mutableListOf()

    init {
        _allProducts.addAll(allProducts!!)
    }
}

