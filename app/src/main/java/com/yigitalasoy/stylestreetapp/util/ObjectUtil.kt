package com.yigitalasoy.stylestreetapp.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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


    inline fun <reified T> objectToJsonString(obj: T?): String? {
        val gson = Gson()
        return gson.toJson(obj)
    }

    inline fun <reified T> jsonStringToObject(jsonString: String?): T? {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<T>() {}.type)
    }

}