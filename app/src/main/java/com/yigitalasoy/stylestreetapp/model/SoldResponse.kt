package com.yigitalasoy.stylestreetapp.model

import com.google.firebase.firestore.PropertyName

data class SoldResponse(
    @get:PropertyName("Sold_Id") @set:PropertyName("Sold_Id") var soldId: String? = "",
    @get:PropertyName("User_Id") @set:PropertyName("User_Id") var userId: String? = "",
    @get:PropertyName("Total_Price") @set:PropertyName("Total_Price") var totalPrice: String? = "",
    @get:PropertyName("Sold_Date") @set:PropertyName("Sold_Date") var soldDate: String? = "",
    @get:PropertyName("Delivery_Status") @set:PropertyName("Delivery_Status") var delivertStatus: String? = "",
    @get:PropertyName("Address_Id") @set:PropertyName("Address_Id") var addressId: String? = "",
    @get:PropertyName("LastUpdate") @set:PropertyName("LastUpdate") var lastUpdate: String? = "",
    @get:PropertyName("Payment_Validation") @set:PropertyName("Payment_Validation") var paymentValidation: Boolean? = false,
)
