package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface AddressRepository {
    suspend fun getUserAddress(userId: String): Resource<ArrayList<AddressResponse>>

    suspend fun addAddress(newAddress: AddressResponse): Resource<AddressResponse>

}