package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryResponse (
    @get:PropertyName("Category_Id") @set:PropertyName("Category_Id") var categoryId:String? = "",
    @get:PropertyName("Category_Name") @set:PropertyName("Category_Name") var categoryName:String? = "",
    @get:PropertyName("Category_Image") @set:PropertyName("Category_Image") var categoryImage:String? = ""
): Parcelable