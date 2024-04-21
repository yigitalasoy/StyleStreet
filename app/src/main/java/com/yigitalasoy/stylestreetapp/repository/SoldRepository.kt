package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldDetailResponse
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface SoldRepository {
    suspend fun getUserSold(userId: String): Resource<ArrayList<SoldResponse>>

    suspend fun getUserSoldDetail(userId: String): Resource<ArrayList<SoldDetailResponse>>

    suspend fun addUserSold(sold: SoldResponse): Resource<SoldResponse>

    suspend fun addUserSoldDetail(userId: String,soldId: String,basketProducts: ArrayList<BasketDetailResponse>): Resource<ArrayList<SoldDetailResponse>>

}