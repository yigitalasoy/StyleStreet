package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductColorResponse (
    @get:PropertyName("Color_Id") @set:PropertyName("Color_Id") var colorId: String = "",
    @get:PropertyName("Color_Name") @set:PropertyName("Color_Name") var colorName: String? = "",
    @get:PropertyName("Color_Code") @set:PropertyName("Color_Code") var colorCode: String? = ""
): Parcelable