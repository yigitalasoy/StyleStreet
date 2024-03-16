package com.yigitalasoy.stylestreetapp.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Resource

interface UserRepository {
    // kullanıcı ile ilgili yapılacak fonksiyonlar sadece tanımlanır. repositoryimp classında ise bu fonksiyonları override ederek içini doldururuz

    suspend fun userLogin(email: String,password: String): Resource<FirebaseUser?>

    suspend fun getUserDataFromDatabase(userUid: String?): Resource<UserResponse>

    suspend fun userSignUp(user: UserResponse): Resource<UserResponse>

    suspend fun registerUserDatabase(user: UserResponse): Resource<UserResponse>

    suspend fun loginWithGoogle(account: GoogleSignInAccount?): Resource<UserResponse>

}