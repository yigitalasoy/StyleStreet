package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface ProductSizeRepository {
    suspend fun getAllProductSize(): Resource<ArrayList<ProductSizeResponse>>
}