package com.yigitalasoy.stylestreetapp.util

import com.google.gson.Gson
import com.yigitalasoy.stylestreetapp.model.AddressResponse

class ObjectUtil {

    fun addressToJsonString(address: AddressResponse?): String? {
        val gson = Gson()
        return gson.toJson(address)
    }

    fun jsonStringToAddress(addressString: String?): AddressResponse? {
        val gson = Gson()
        return gson.fromJson(addressString,AddressResponse::class.java)
    }

}