package com.yigitalasoy.stylestreetapp.ui.fragment.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentLoginBinding
import com.yigitalasoy.stylestreetapp.ui.activity.main.MainActivity
import com.yigitalasoy.stylestreetapp.util.Constants
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productColorViewModel: ProductColorViewModel
    @Inject lateinit var productSizeViewModel: ProductSizeViewModel
    @Inject lateinit var basketViewModel: BasketViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context: Context = requireContext() // Uygulamanın Context'i
        if (isInternetConnected(context)) {
            println("İnternet bağlantısı var.")

            productViewModel.getNewInProduct()
            productViewModel.getAllProduct()

            binding?.apply {
                textViewSignUp.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
                }

                imageViewEye.setOnClickListener {
                    if(editTextPassword.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())){
                        editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        imageViewEye.setImageResource(R.drawable.eye_show)
                    } else {
                        editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        imageViewEye.setImageResource(R.drawable.eye_hide)
                    }
                }

                buttonLoginWithGoogle.setOnClickListener {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.googleServerClientId))
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(it.context,gso)

                    val signInClient = googleSignInClient.signInIntent
                    startActivityForResult(signInClient,Constants.GOOGLE_SIGN_IN_REQUEST_CODE)

                }

                buttonLogin.setOnClickListener {

                    try {
                        userViewModel.userLogin(editTextEmail.text.toString(),editTextPassword.text.toString())
                        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    } catch (e: Exception){
                        println(e.printStackTrace())
                    }
                }

                textViewForgotPassword.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
                }


            }
            observer()

        } else {
            println("İnternet bağlantısı yok.")
        }

        // productViewModel.getNewInProduct()
        // productColorViewModel.getAllProductColors()
        // productSizeViewModel.getAllProductSize()

        /*if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            Log.e("login","başarıyla çıkış yapıldı")
        } else {
            Log.e("login","çıkış yapılmadı. kayıtlı hesap yok")
        }*/



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.GOOGLE_SIGN_IN_REQUEST_CODE -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    userViewModel.loginWithGoogle(account)
                } catch (e: ApiException){
                    Log.e("GOOGLE LOGIN ERROR",e.stackTraceToString())
                    e.printStackTrace()
                }
            }
        }
    }


    fun observer(){

        userViewModel.userLiveData.observe(viewLifecycleOwner){
            println("login fragment userViewModel.userLiveData.observe")
            println(it)
            when(it.status){
                Status.SUCCESS -> {
                    Log.i("user data success","")

                    binding.textViewError.hide()
                    binding.progressBarLoading.hide()
                    println("user login observerrrrrrrrrrrrr")

                    if(it.data != null){
                        this.toast("Login succesfully")
                        println("login success")
                        val mainActivity = Intent(activity, MainActivity::class.java)
                        startActivity(mainActivity)
                        activity?.finish()

                    }
                }
                Status.ERROR -> {
                    Log.i("user data error",it.message.toString())

                    binding.progressBarLoading.hide()
                    binding.textViewError.show()
                    this.toast(it.message!!)
                }
                Status.LOADING -> {
                    Log.i("user data loading","")
                    binding.progressBarLoading.show()
                }
            }


        }
    }


    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }
        @Suppress("DEPRECATION")
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


}