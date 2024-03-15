package com.yigitalasoy.stylestreetapp.repository

import com.yigitalasoy.stylestreetapp.model.UserResponse

interface UserRepository {
    // kullanıcı ile ilgili yapılacak fonksiyonlar sadece tanımlanır. repositoryimp classında ise bu fonksiyonları override ederek içini doldururuz

    fun userLogin(email: String,password: String)


    fun getUserDataFromDatabase(userUid: String?)

    fun userSignUp(user: UserResponse)


    fun registerUserDatabase(user: UserResponse)


    fun loginWithGoogle()

}