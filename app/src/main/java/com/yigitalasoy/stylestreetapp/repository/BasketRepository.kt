package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface BasketRepository {

    suspend fun getUserBasket(userId: String): Resource<BasketResponse>

    suspend fun productQuantityChange(userId: String,subProductId: String,type: String,position: Int): Boolean

    suspend fun productRemove(userId: String,subProductId: String,type: String,position: Int): Boolean

    suspend fun addProduct(userId: String,basketItem: BasketResponse): HashMap<String,Any>

    suspend fun removeAllProducts(userId: String,basketData: BasketResponse): Boolean

}