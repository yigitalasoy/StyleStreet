package com.yigitalasoy.stylestreetapp.ui.fragment.signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.databinding.FragmentSignUpBinding
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var userViewModel: UserViewModel

    var isSevenChar: Boolean = false
    var isBigChar: Boolean = false
    var isNumber: Boolean = false
    var emailValidation: Boolean = false
    private var container: ViewGroup? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        this.container = container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()

        binding.apply {

            imageViewBackButton.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

            imageViewEye.setOnClickListener {
                hideShowPassword(editTextPassword,imageViewEye)
            }

            editTextPassword.doOnTextChanged { text, start, before, count ->
                passwordCheckRequirements()
            }

            editTextEmail.doOnTextChanged { text, start, before, count ->
                checkEmailValidation()
            }

            buttonSignUp.setOnClickListener {
                if(!emailValidation && !isBigChar && !isNumber && !isSevenChar){
                    //kayıt olma işlemi yapılacak
                    Log.e("KAYIT","KAYIT GEREKLİLİKLERİ SAĞLANDI")

                    userViewModel.userSignUp(getUser())


                } else {
                    Log.e("KAYIT","KAYIT GEREKLİLİKLERİ SAĞLANMADI")
                }
            }
        }

        observer()

    }

    private fun init() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    fun getUser(): UserResponse {
        return UserResponse(
            id = "",
            name = binding.editTextName.text.toString(),
            surname = binding.editTextSurname.text.toString(),
            email = binding.editTextEmail.text.toString(),
            password = binding.editTextPassword.text.toString()
        )
    }

    fun observer(){

        userViewModel.userLiveData.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(context,"Succesfully registered", Toast.LENGTH_LONG).show()
                this.view?.let {view ->
                    val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        userViewModel.isLogin.observe(viewLifecycleOwner){
            it?.let {
                //false olduğu zaman çıkış yapılmış olacak
            }
        }

        userViewModel.userError.observe(viewLifecycleOwner){
            it?.let {
                if(it.message.equals("REGISTER SUCCESS")){
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    this.toast("Register successfully")
                }
                if(it.data!!){
                    binding.textViewSignupError.show()
                } else {
                    binding.textViewSignupError.hide()
                }
            }
        }

        userViewModel.userLoading.observe(viewLifecycleOwner){
            it?.let {
                if(it.data!!){
                    binding.progressBarSignupLoading.show()
                } else {
                    binding.progressBarSignupLoading.hide()
                }
            }
        }




    }

    fun passwordCheckRequirements(){
        binding.apply {
            if ((editTextPassword.text.toString()!!.length >= 7)) {
                isSevenChar = true

                for (char in editTextPassword.text.toString()) {
                    if (char.isUpperCase()) {
                        isBigChar = true
                        break
                    } else {
                        isBigChar = false
                    }
                }

                for (char in editTextPassword.text.toString()) {
                    if (char.isDigit()) {
                        isNumber = true
                        break
                    } else {
                        isNumber = false
                    }
                }
            } else {
                isSevenChar = false
            }

            if (isSevenChar && isBigChar && isNumber) {
                editTextPassword.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.green))
                textViewPasswordRequirements.setTextColor(resources.getColor(R.color.green))
            } else {
                editTextPassword.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.red))
                textViewPasswordRequirements.setTextColor(resources.getColor(R.color.red))

            }
        }

    }



    fun checkEmailValidation() {
        val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"

        binding.apply {
            emailValidation = editTextEmail.text.toString().matches(emailRegex.toRegex())

            if (emailValidation) {
                editTextEmail.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.green))
            } else {
                editTextEmail.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.red))
            }
        }
    }


    fun hideShowPassword(editText: EditText,imageView: ImageView){
        Log.e("ERROR","basıldı")
        binding.apply {
            if(editText.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())){
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView.setImageResource(R.drawable.eye_show)
            } else {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView.setImageResource(R.drawable.eye_hide)
            }
        }
    }

}