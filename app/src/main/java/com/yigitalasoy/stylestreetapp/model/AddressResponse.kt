package com.yigitalasoy.stylestreetapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressResponse (
    @get:PropertyName("User_Id") @set:PropertyName("User_Id") var userId:String? = "",
    @get:PropertyName("Address_Id") @set:PropertyName("Address_Id") var addressId:String? = "",
    @get:PropertyName("Address_Detail") @set:PropertyName("Address_Detail") var addressDetail:String? = "",
    @get:PropertyName("Address_Header") @set:PropertyName("Address_Header") var addressHeader:String? = "",
    @get:PropertyName("Address_District") @set:PropertyName("Address_District") var addressDistrict:String? = "",
    @get:PropertyName("Address_Province") @set:PropertyName("Address_Province") var addressProvince:String? = "",
    @get:PropertyName("Tel_Number") @set:PropertyName("Tel_Number") var telNumber:String? = "",
    @get:PropertyName("Address_Name") @set:PropertyName("Address_Name") var addressName:String? = "",
    @get:PropertyName("Address_Surname") @set:PropertyName("Address_Surname") var addressSurname:String? = ""
): Parcelable