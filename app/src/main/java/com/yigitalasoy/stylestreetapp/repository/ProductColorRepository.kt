package com.yigitalasoy.stylestreetapp.repository


import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface ProductColorRepository {
    suspend fun getAllProductColor(): Resource<ArrayList<ProductColorResponse>>
}