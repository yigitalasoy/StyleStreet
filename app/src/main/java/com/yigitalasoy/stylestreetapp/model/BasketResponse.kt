package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class BasketResponse(
    @set:PropertyName("Basket_Id") var basketId: String?,
    @set:PropertyName("User_Id") var userId: String?,
    @set:PropertyName("Basket_Products") var basketProducts: @RawValue ArrayList<BasketDetailResponse>?
): Parcelable