package com.yigitalasoy.stylestreetapp.util

import com.google.gson.Gson
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.model.SoldResponse

class ObjectUtil {

    fun addressToJsonString(address: AddressResponse?): String? {
        val gson = Gson()
        return gson.toJson(address)
    }

    fun jsonStringToAddress(addressString: String?): AddressResponse? {
        val gson = Gson()
        return gson.fromJson(addressString,AddressResponse::class.java)
    }

    fun soldToJsonString(sold: SoldResponse?): String? {
        val gson = Gson()
        return gson.toJson(sold)
    }

    fun jsonStringToSold(soldString: String?): SoldResponse? {
        val gson = Gson()
        return gson.fromJson(soldString,SoldResponse::class.java)
    }

}