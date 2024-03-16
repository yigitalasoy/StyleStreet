package com.yigitalasoy.stylestreetapp.util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yigitalasoy.stylestreetapp.model.UserResponse

fun Map<String, Any>.mapToObject(): UserResponse {
    return UserResponse(
        id = this["id"].toString(),
        name = this["name"].toString(),
        surname = this["surname"].toString(),
        email = this["email"].toString(),
        password = this["password"].toString()
    )
}

fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun Fragment.toast(text: String){
    Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
}