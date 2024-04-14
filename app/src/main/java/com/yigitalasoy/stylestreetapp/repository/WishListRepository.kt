package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.WishListResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface WishListRepository {

    suspend fun getWishList(userId: String): Resource<ArrayList<WishListResponse>>

    suspend fun addProductToWishList(userId: String,productId: String): HashMap<String,Any>

    suspend fun removeProductToWishList(wishId: String): Boolean



}