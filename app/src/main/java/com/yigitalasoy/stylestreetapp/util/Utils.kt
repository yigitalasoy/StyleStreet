package com.yigitalasoy.stylestreetapp.util

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

/*fun Map<String, Any>.mapToObject(): UserResponse {
    return UserResponse(
        id = this["id"].toString(),
        name = this["name"].toString(),
        surname = this["surname"].toString(),
        email = this["email"].toString(),
        password = this["password"].toString()
    )
}*/

fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun Fragment.toast(text: String){
    Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
}

fun Activity.toast(text: String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}

fun ImageView.downloadImage(imageUrl: String){
    Glide.with(context)
        .load(imageUrl)
        .into(this)
}
