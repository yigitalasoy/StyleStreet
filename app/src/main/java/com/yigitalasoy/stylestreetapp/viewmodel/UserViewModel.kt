package com.yigitalasoy.stylestreetapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.mapToObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserResponse>>()
    val userLoading = MutableLiveData<Resource<Boolean>>()
    val userError = MutableLiveData<Resource<Boolean>>()
    val isLogin = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null

    private var auth = Firebase.auth
    private var db = Firebase.firestore

    private lateinit var mAuth: FirebaseAuth


    private lateinit var mGoogleSignInClient: GoogleSignInClient

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        userError.value = Resource.error(throwable.localizedMessage ?: "error!",data = true)
    }



    fun userLogin(email: String,password: String){

        userLoading.value = Resource.loading(true)
        userError.value = Resource.error("",false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val firebaseUser = userRepository.userLogin(email,password)
            Log.e("FIREBASE LOGIN: ","${firebaseUser.message} ID=${firebaseUser.data?.uid}")
            val databaseUser = userRepository.getUserDataFromDatabase(firebaseUser.data?.uid)
            Log.e("DATABASE LOGIN: ","${databaseUser.message} ID=${databaseUser.data?.id}")

            withContext(Dispatchers.Main){
                if(firebaseUser.data==null || databaseUser.data==null){
                    userError.value = Resource.error("Error",true)
                    userLoading.value = Resource.loading(false)
                } else {
                    userLiveData.value = databaseUser
                    isLogin.value = Resource.success(true)
                    userLoading.value = Resource.loading(false)
                    userError.value = Resource.error("Error",false)
                }
            }
        }

        //giriş işlemi yapılacak
        //giriş doğru ise database'deki kullanıcı verilerini de alıp userLiveData' ya aktarılacak.
    }

    fun userSignUp(user: UserResponse){
        //kayıt işlemi yapılacak
        userLoading.value = Resource.loading(true)
        userError.value = Resource.error("Error",false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val registeredUser = userRepository.userSignUp(user)

            withContext(Dispatchers.Main){
                if(registeredUser.data != null){
                    userError.value = Resource.error("REGISTER SUCCESS",false)
                    userLoading.value = Resource.loading(false)
                } else {
                    userError.value = Resource.error("Error Firebase register: ${registeredUser.message}",true)
                }
            }
        }


/*
        auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val firebaseUser = auth.currentUser
                    println("REGISTERED USER UUID: ${firebaseUser?.uid}")

                    user.id = firebaseUser?.uid
                    registerUserDatabase(user)

                    userLiveData.value = Resource.success(user)
                    userLoading.value = Resource.loading(false)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    userError.value = Resource.error("Error",false)
                    userLoading.value = Resource.loading(false)

                }
            }
*/
    }


    fun loginWithGoogle(account: GoogleSignInAccount?) {
        userLoading.value = Resource.loading(true)
        userError.value = Resource.error("Error",false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val googleLoginUser = userRepository.loginWithGoogle(account)

            withContext(Dispatchers.Main){
                if(googleLoginUser.data != null){
                    Log.e("REGISTER SUCCESS","user email: ${googleLoginUser.data.email}")
                    userError.value = Resource.error("REGISTER SUCCESS",false)
                    userLoading.value = Resource.loading(false)
                    userLiveData.value = googleLoginUser
                    isLogin.value = Resource.success(true)
                } else {
                    userError.value = Resource.error("Error Firebase register: ${googleLoginUser.message}",true)
                }
            }
        }



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