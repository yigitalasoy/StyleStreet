package com.yigitalasoy.stylestreetapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.repository.UserRepository
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository,val auth: FirebaseAuth,val db: FirebaseFirestore): ViewModel() {

    val userLiveData = MutableLiveData<Resource<UserResponse>>()
    val userLoading = MutableLiveData<Resource<Boolean>>()
    val userError = MutableLiveData<Resource<Boolean>>()
    //val isLogin = MutableLiveData<Resource<Boolean>>()

    private var job : Job? = null


    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        liveDataError(throwable.localizedMessage ?: "error!",data = true)
    }



    fun userLogin(email: String,password: String){

        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val firebaseUser = userRepository.userLogin(email,password)
            Log.e("FIREBASE LOGIN: ","${firebaseUser.message} ID=${firebaseUser.data?.uid}")
            val databaseUser = userRepository.getUserDataFromDatabase(firebaseUser.data?.uid)
            Log.e("DATABASE LOGIN: ","${databaseUser.message} ID=${databaseUser.data?.id}")

            withContext(Dispatchers.Main){


                when (firebaseUser.status) {
                    Status.SUCCESS ->{
                        if(databaseUser.status == Status.SUCCESS){
                            userLiveData.value = databaseUser
                            println("user live data değişti: ${userLiveData.value!!.data}")
                            //isLogin.value = Resource.success(true)
                            liveDataLoading(false)
                            //liveDataError("userLogin", false)
                        } else {
                            liveDataError(databaseUser.message, false)
                        }
                    }

                    Status.ERROR -> {
                        liveDataError(firebaseUser.message, true)
                        liveDataLoading(false)
                    }
                    else -> {

                    }
                }


                /*if(firebaseUser.data==null || databaseUser.data==null){
                    liveDataError("Error", true)
                    liveDataLoading(false)
                } else {
                    userLiveData.value = databaseUser
                    println("user live data değişti: ${userLiveData.value!!.data}")
                    //isLogin.value = Resource.success(true)
                    liveDataLoading(false)
                    liveDataError("userLogin", false)
                }*/
            }
        }

        //giriş işlemi yapılacak
        //giriş doğru ise database'deki kullanıcı verilerini de alıp userLiveData' ya aktarılacak.
    }

    fun userSignUp(user: UserResponse){
        //kayıt işlemi yapılacak
        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val registeredUser = userRepository.userSignUp(user)

            withContext(Dispatchers.Main){
                when (registeredUser.status) {
                    Status.SUCCESS ->{
                        println("user success")
                        liveDataError("REGISTER SUCCESS",false)
                        liveDataLoading(false)
                    }

                    Status.ERROR -> {
                        liveDataLoading(false)
                        liveDataError("Error Firebase register: ${registeredUser.message}",true)
                    }

                    else -> {
                        println("else")
                    }
                }

                /*if(registeredUser.data != null){
                    liveDataError("REGISTER SUCCESS",false)
                    liveDataLoading(false)
                } else {
                    liveDataError("Error Firebase register: ${registeredUser.message}",true)
                }*/
            }
        }
    }


    fun loginWithGoogle(account: GoogleSignInAccount?) {
        liveDataLoading(true)
        liveDataError(data = false)

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            //IO'da sunucu işlemleri yapılır
            val googleLoginUser = userRepository.loginWithGoogle(account)

            withContext(Dispatchers.Main){
                if(googleLoginUser.data != null){
                    Log.e("REGISTER SUCCESS","user email: ${googleLoginUser.data}")
                    liveDataError("REGISTER SUCCESS",false)
                    liveDataLoading(false)
                    userLiveData.value = googleLoginUser
                    println("user live data değişti: ${userLiveData.value!!.data}")
                    //isLogin.value = Resource.success(true)
                } else {
                    liveDataError("Error Firebase register: ${googleLoginUser.message}",true)
                }
            }
        }
    }

    private fun liveDataLoading(data: Boolean) {
        userLoading.value = Resource.loading(data)
    }

    private fun liveDataError(message: String? = "",data: Boolean) {
        userError.value = Resource.error(message!!,data)
    }


    fun signOut(context: Context){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(context,R.string.googleServerClientId))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context,gso)
        googleSignInClient.signOut().addOnCompleteListener {
            Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
        }

        auth.signOut()

        //isLogin.value = Resource.success(false)
        userLiveData.value = Resource.success(null)

    }

    fun sendResetPasswordLink(email: String, fragment: Fragment){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val resetPasswordState = userRepository.resetPassword(email)

            withContext(Dispatchers.Main){
                if(resetPasswordState){
                    Log.e("LINK SEND","user email: ${email}")

                    println("LINK SEND: ${email}")
                } else {
                    //fragment.toast("DOES NOT EXIST EMAIL")
                    println("LINK error: ${email}")
                }
            }
        }

    }






}