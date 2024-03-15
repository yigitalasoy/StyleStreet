package com.yigitalasoy.stylestreetapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.mapToObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {

    val userLiveData = MutableLiveData<UserResponse>()
    val userLoading = MutableLiveData<Boolean>()
    val userError = MutableLiveData<Boolean>()
    val isLogin = MutableLiveData<Boolean>()

    private var auth = Firebase.auth
    private var db = Firebase.firestore

    private lateinit var mAuth: FirebaseAuth


    private lateinit var mGoogleSignInClient: GoogleSignInClient



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

        val docRef = db.collection(Constants.FIRESTORE_DATABASE_DOCUMENT_ID).document(userUid.toString())
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


        db.collection(Constants.FIRESTORE_DATABASE_DOCUMENT_ID).document(user.id.toString())
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

        /*

        val mauth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.firebaseWebApiKey.toString())
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(getApplication(), gso)

        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            Log.e("LOGIN WITH GOOGLE","SUCCESFULLY LOGIN: ${userName}")
        } else {
            Log.e("LOGIN WITH GOOGLE","ERROR")
        }

        */

/*
        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.firebaseWebApiKey.toString())
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(getApplication(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
*/

    }


    companion object {
        private const val RC_SIGN_IN = 9001
    }


}