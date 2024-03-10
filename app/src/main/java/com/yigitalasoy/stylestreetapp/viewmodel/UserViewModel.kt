package com.yigitalasoy.stylestreetapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.util.FireStoreUtil
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.mapToObject



class UserViewModel(application: Application): BaseViewModel(application) {

    val userLiveData = MutableLiveData<UserResponse>()
    val userLoading = MutableLiveData<Boolean>()
    val userError = MutableLiveData<Boolean>()
    val isLogin = MutableLiveData<Boolean>()

    private var auth = Firebase.auth
    private var db = Firebase.firestore


    fun userLogin(email: String,password: String){
        userError.value = false
        userLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Log.d("fun userLogin", "signInWithEmail:success")
                    val firebaseUser = auth.currentUser
                    getUserDataFromDatabase(firebaseUser?.uid)

                    userError.value = false
                    userLoading.value = false

                } else {
                    Log.w("fun userLogin", "signInWithEmail:failure", task.exception)
                    userError.value = true
                    userLoading.value = false
                }
            }

        //giriş işlemi yapılacak
        //giriş doğru ise database'deki kullanıcı verilerini de alıp userLiveData' ya aktarılacak.
    }

    private fun getUserDataFromDatabase(userUid: String?) {

        //firecloud'dan user data alınacak

        val docRef = db.collection(FireStoreUtil.FIRESTORE_DATABASE_DOCUMENT_ID).document(userUid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userLiveData.value = document.data?.mapToObject()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    fun userSignUp(user: UserResponse){
        //kayıt işlemi yapılacak
        userLoading.value = true
        userError.value = false

        auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val firebaseUser = auth.currentUser
                    println("REGISTERED USER UUID: ${firebaseUser?.uid}")

                    user.id = firebaseUser?.uid
                    registerUserDatabase(user)

                    userLiveData.value = user
                    userLoading.value = false
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    userError.value = true
                    userLoading.value = false

                }
            }
    }

    fun registerUserDatabase(user: UserResponse) {
        //firestore database kullanıcı kaydı yapılacak
        userLoading.value = true


        db.collection(FireStoreUtil.FIRESTORE_DATABASE_DOCUMENT_ID).document(user.id.toString())
            .set(user)
            .addOnSuccessListener {
                userLoading.value = false
                println("USER SUCCESFULLY ADDED DATABASE: ${user.id}")
            }
            .addOnFailureListener {
                userError.value = true
                Log.e("ERROR",it.message.toString())
            }


    }


    fun loginWithGoogle() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(R.string.firebaseWebApiKey.toString())
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
    }


}