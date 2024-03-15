package com.yigitalasoy.stylestreetapp.util

import com.yigitalasoy.stylestreetapp.model.UserResponse

fun Map<String, Any>.mapToObject(): UserResponse {
    return UserResponse(
        id = this["id"].toString(),
        name = this["name"].toString(),
        surname = this["surname"].toString(),
        email = this["email"].toString(),
        password = this["password"].toString()
    )
}