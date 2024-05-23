package com.yigitalasoy.stylestreetapp.repository

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Resource
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class UserRepositoryImp(val auth: FirebaseAuth,val db: FirebaseFirestore, val storage: FirebaseStorage): UserRepository {
    override suspend fun userLogin(email: String, password: String): Resource<FirebaseUser?> {
        val firebaseUser: FirebaseUser?

        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                Resource.success(firebaseUser)
                Log.d("fun userLogin", "signInWithEmail:success start")
                firebaseUser?.let {
                    return@let Resource.success(auth.currentUser)
                } ?: Resource.error("USER NOT FOUND",null)

            } else {
                Resource.error("USER NOT FOUND",null)
            }
        } catch (e: Exception){
            Resource.error(e.message.toString(),null)
        }
    }

    override suspend fun getUserDataFromDatabase(userUid: String?): Resource<UserResponse> {

        return try {
            val docRef = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(userUid.toString())
            val document = docRef.get().await()


            if (document != null) {
                println("user: ${document.toObject(UserResponse::class.java)}")
                Resource.success(document.toObject(UserResponse::class.java))
            } else {
                Log.d(ContentValues.TAG, "No such document")
                Resource.error("No such document",null)

            }
        } catch (e: Exception){
            Resource.error(e.message.toString(),null)
        }

    }

    override suspend fun userSignUp(user: UserResponse, photoBitmap: Bitmap): Resource<UserResponse> {
        return try {
            val task = auth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            task.await()
            if (task.isSuccessful) {
                Log.e("error user sign up", "createUserWithEmail:success")
                val firebaseUser = auth.currentUser
                println("REGISTERED USER UUID: ${firebaseUser?.uid}")

                val storageRef = storage.reference

                val riversRef = storageRef.child("images/users/${firebaseUser?.uid}")

                val baos = ByteArrayOutputStream()
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = riversRef.putBytes(data)

                uploadTask.await()

                if(uploadTask.isSuccessful){
                    println("RESİM UPLOAD SUCCESS")
                    println("DOWNLOAD URL: ${riversRef.downloadUrl}")

                    val photoState = riversRef.getDownloadUrl()
                    photoState.await()

                    if(photoState.isSuccessful){
                        user.userImageURL = photoState.result.toString()
                    } else {
                        println("RESİM URİ FAİL ${photoState.exception.toString()}")
                    }

                } else {
                    println("RESİM UPLOAD FAİL ${uploadTask.exception.toString()}")
                }

                user.id = firebaseUser?.uid
                user.loginType = Constants.PASSWORD_LOGIN_TYPE
                registerUserDatabase(user)

                Resource.success(user)

            } else {
                Log.e(ContentValues.TAG, "createUserWithEmail:failure")
                Resource.error(task.exception?.message.toString(),null)
            }

        } catch (e: Exception){
            Resource.error(e.message.toString(),null)
        }
    }

    override suspend fun registerUserDatabase(user: UserResponse): Resource<UserResponse> {

        return try {
            val doc = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(user.id.toString()).set(user)
            doc.await()

            val collectionRef = db.collection("tbl_Basket")

            val a = collectionRef.document(user.id.toString()).get()
            a.await()
            if(a.result.data == null){
                var newBasketId = collectionRef.document().id

                db.collection("tbl_Basket").document(user.id.toString()).set(hashMapOf(
                    "Basket_Id" to newBasketId,
                    "User_Id" to user.id.toString(),
                    "Basket_Products" to arrayListOf<HashMap<String,Any>>()
                ))
            }

            if(doc.isSuccessful){
                Log.e("database added","USER SUCCESFULLY ADDED DATABASE: ${user.id}")
                Resource.success(user)
            } else {
                Log.e("ERROR", doc.exception!!.message!!)
                Resource.error(doc.exception!!.message!!,null)
            }

        } catch (e: Exception){
            Resource.error("${e.message}",null)
        }
    }

    override suspend fun loginWithGoogle(account: GoogleSignInAccount?): Resource<UserResponse> {

        return try {

            val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
            val googleLogin = FirebaseAuth.getInstance().signInWithCredential(credential)
            googleLogin.await()


            if(googleLogin.isSuccessful){
                Log.e("GOOGLE SIGN IN","google sign in success")
                account!!.let {


                    //eğer kullanıcı yoksa register yapıcak.
                    val doc = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(auth.currentUser?.uid.toString()).get()
                    doc.await()
                    if(doc.result.data == null) {
                        println("NULL GELDİ USER GOOGLE LOGİN")
                        val user = UserResponse(
                            id = auth.currentUser?.uid,
                            name = account.givenName.toString(),
                            surname = account.familyName.toString(),
                            email = account.email.toString(),
                            password = "",
                            userImageURL = account.photoUrl.toString(),
                        )
                        user.loginType = Constants.GOOGLE_LOGIN_TYPE

                        return@let registerUserDatabase(user)
                    } else {
                        println("NULL GELMEDİİİ USER GOOGLE LOGİN")
                        return@let getUserDataFromDatabase(auth.currentUser?.uid)
                    }
                }
            } else {
                Log.e("GOOGLE SIGN IN ERROR",googleLogin.exception?.message.toString())
                Resource.error(googleLogin.exception?.message.toString(),null)

            }

        } catch (e: Exception){
            Resource.error(e.message.toString(),null)
        }
    }

    override suspend fun resetPassword(email: String): Boolean {
        return try {
            val a = auth.sendPasswordResetEmail(email)
            a.isSuccessful
        } catch (e: Exception){
            false
        }
    }

    override suspend fun updateUser(user: UserResponse?,activity: Activity,bitmap: Bitmap): HashMap<String,Any>? {


        val firebaseUser = auth.currentUser
        user?.id = firebaseUser?.uid
        var taskPassword: Task<Void>? = null
        var passwordState = false
        if (user?.loginType.equals(Constants.PASSWORD_LOGIN_TYPE)){
            taskPassword = firebaseUser!!.updatePassword(user?.password.toString())
            taskPassword.await()
            passwordState = taskPassword.isSuccessful
        }


        /*val stateUser = firebaseUser!!.updateProfile(userProfileChangeRequest {
            displayName = user?.name + " " + user?.surname
        })*/


        val storageRef = storage.reference

        val riversRef = storageRef.child("images/users/${firebaseUser?.uid}")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = riversRef.putBytes(data)

        uploadTask.await()

        if(uploadTask.isSuccessful){
            println("RESİM UPLOAD SUCCESS")
            println("DOWNLOAD URL: ${riversRef.downloadUrl}")

            val photoState = riversRef.getDownloadUrl()
            photoState.await()

            if(photoState.isSuccessful){
                println("RESİM URİ SUCCESS")


                var doc: Task<Void>? = null
                if( (user?.loginType.equals(Constants.GOOGLE_LOGIN_TYPE)) || (user?.loginType.equals(Constants.PASSWORD_LOGIN_TYPE) && passwordState)){
                    doc = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(user?.id.toString()).set(user!!)
                    doc.await()
                }

                user?.userImageURL = photoState.result.toString()

                val stateUser = db.collection(Constants.FIRESTORE_DATABASE_USERS).document(user?.id.toString()).set(user!!)
                stateUser.await()

                if(stateUser.isSuccessful){
                    return hashMapOf(
                        "Task" to doc!!.isSuccessful,
                        "URL" to photoState.result.toString()
                    )
                } else {
                    println("DATABASE UPDATE EDİLMEDİ")
                }

            } else {
                println("RESİM URİ FAİL ${photoState.exception.toString()}")
            }

        } else {
            println("RESİM UPLOAD FAİL ${uploadTask.exception.toString()}")
        }

        return null

    }

}