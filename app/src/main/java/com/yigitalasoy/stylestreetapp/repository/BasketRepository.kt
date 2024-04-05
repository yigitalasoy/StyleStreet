package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface BasketRepository {

    suspend fun getUserBasket(userId: String): Resource<BasketResponse>

}