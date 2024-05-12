package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @get:PropertyName("id") @set:PropertyName("id") var id: String? = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String? = "",
    @get:PropertyName("surname") @set:PropertyName("surname") var surname: String? = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String? = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String? = "",
    @get:PropertyName("imageURL") @set:PropertyName("imageURL") var userImageURL: String? = "",
    @get:PropertyName("telephone") @set:PropertyName("telephone") var telephone: String? = ""
): Parcelable {
    @IgnoredOnParcel
    var loginType: String? = ""
}
