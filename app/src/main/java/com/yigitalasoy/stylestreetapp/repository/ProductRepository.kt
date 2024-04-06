package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface ProductRepository {
    suspend fun getTopSellingProduct(): Resource<ArrayList<ProductResponse>>
    suspend fun getNewInProduct(): Resource<ArrayList<ProductResponse>>
    suspend fun getAllProduct(): Resource<ArrayList<ProductResponse>>
    suspend fun getSearchedProduct(search: String): Resource<ArrayList<ProductResponse>>
    suspend fun getProductsWithId(productId: String): Resource<ArrayList<ProductResponse>>
}