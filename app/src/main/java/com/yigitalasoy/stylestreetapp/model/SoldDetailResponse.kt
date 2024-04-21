package com.yigitalasoy.stylestreetapp.model

import com.google.firebase.firestore.PropertyName

data class SoldDetailResponse(
    @get:PropertyName("SoldDetail_Id") @set:PropertyName("SoldDetail_Id") var soldDetailId: String? = "",
    @get:PropertyName("Sold_Id") @set:PropertyName("Sold_Id") var soldId: String? = "",
    @get:PropertyName("SubProduct_Id") @set:PropertyName("SubProduct_Id") var subProductId: String? = "",
    @get:PropertyName("Quantity") @set:PropertyName("Quantity") var quantity: String? = "",
    @get:PropertyName("User_Id") @set:PropertyName("User_Id") var userId: String? = ""
)
