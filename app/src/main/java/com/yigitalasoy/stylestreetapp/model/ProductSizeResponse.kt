package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductSizeResponse (
    @get:PropertyName("ProductSize_Id") @set:PropertyName("ProductSize_Id") var productSizeId: String? = "",
    @get:PropertyName("ProductSize_Name") @set:PropertyName("ProductSize_Name") var productSizeName: String? = ""
):Parcelable