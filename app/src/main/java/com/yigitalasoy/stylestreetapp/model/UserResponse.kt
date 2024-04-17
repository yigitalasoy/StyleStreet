package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @get:PropertyName("id") @set:PropertyName("id") var id: String? = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String? = "",
    @get:PropertyName("surname") @set:PropertyName("surname") var surname: String? = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String? = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String? = ""
): Parcelable
