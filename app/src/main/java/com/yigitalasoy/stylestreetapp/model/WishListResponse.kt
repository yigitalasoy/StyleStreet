package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishListResponse (
    @get:PropertyName("Wish_Id") @set:PropertyName("Wish_Id") var wish_Id: String? = "",
    @get:PropertyName("User_Id") @set:PropertyName("User_Id") var user_Id: String? = "",
    @get:PropertyName("Product_Id") @set:PropertyName("Product_Id") var product_Id: String? = ""
): Parcelable