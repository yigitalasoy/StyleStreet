package com.yigitalasoy.stylestreetapp.ui.fragment.login

import android.content.Context
import android.content.Intent
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
import com.yigitalasoy.stylestreetapp.util.Resource
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

        if(userViewModel.userError.value?.message.equals("Error: REGISTER SUCCESS")){
            userViewModel.userError.value = Resource.error("",null)
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
            it?.let {
                if(it.data != null){
                    this.toast("Login succesfully")

                    val mainActivity = Intent(activity, MainActivity::class.java)
                    startActivity(mainActivity)
                    activity?.finish()

                }
            }
        }

        /*userViewModel.isLogin.observe(viewLifecycleOwner){
            it?.let {
                if(it.data!!){
                    Toast.makeText(context,"Login succesfully islogin", Toast.LENGTH_LONG).show()

                    val mainActivity = Intent(activity, MainActivity::class.java)
                    startActivity(mainActivity)
                    activity?.finish()
                } else {
                    val loginActivity = Intent(activity, LoginActivity::class.java)
                    startActivity(loginActivity)
                    activity?.finish()
                }
            }
        }*/

        userViewModel.userError.observe(viewLifecycleOwner){
            it?.let {
                if(it.data == true){
                    binding.textViewError.show()
                    this.toast(it.message!!)
                } else {
                    binding.textViewError.hide()
                }
            }
        }

        userViewModel.userLoading.observe(viewLifecycleOwner){
            it?.let {
                if(it.data == true){
                    binding.progressBarLoading.show()
                } else {
                    binding.progressBarLoading.hide()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}