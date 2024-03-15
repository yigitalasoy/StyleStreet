package com.yigitalasoy.stylestreetapp.ui.fragment.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentLoginBinding
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.ui.activity.main.MainActivity
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    private val googleSignInRequestCode = 234


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
        init()

        FirebaseAuth.getInstance().signOut()

        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.e("login","başarıyla çıkış yapıldı")
        } else {
            Log.e("login","çıkış yapılmadı")
        }



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
                startActivityForResult(signInClient,googleSignInRequestCode)

            }

            buttonLogin.setOnClickListener {
                userViewModel.userLogin(editTextEmail.text.toString(),editTextPassword.text.toString())
            }

        }

        observer()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            googleSignInRequestCode -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException){
                    Log.e("GOOGLE LOGIN ERROR",e.stackTraceToString())
                    e.printStackTrace()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(context,"GOOGLE LOG IN SUCCESFULLY",Toast.LENGTH_LONG).show()
                Log.e("GOOGLE SIGN IN","google sign in success")

                userViewModel.isLogin.value = true


            }
            .addOnFailureListener {
                Toast.makeText(context,"GOOGLE LOG IN ERROR",Toast.LENGTH_LONG).show()

                Log.e("GOOGLE SIGN IN ERROR",it.message.toString())
            }

    }

    private fun init() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

    }

    fun observer(){

        userViewModel.userLiveData.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(context,"Login succesfully", Toast.LENGTH_LONG).show()
            }
        }

        userViewModel.isLogin.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    /*val fragmentManager = requireActivity().supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()

                    transaction.remove(this)
                    transaction.commit()
                    */

                    val mainActivity = Intent(activity, MainActivity::class.java)
                    startActivity(mainActivity)
                    activity?.finish()


                }
            }
        }

        userViewModel.userError.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding?.textViewError?.visibility = View.VISIBLE
                } else {
                    binding?.textViewError?.visibility = View.INVISIBLE
                }
            }
        }

        userViewModel.userLoading.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding?.progressBarLoading?.visibility = View.VISIBLE
                } else {
                    binding?.progressBarLoading?.visibility = View.INVISIBLE
                }
            }
        }




    }




}