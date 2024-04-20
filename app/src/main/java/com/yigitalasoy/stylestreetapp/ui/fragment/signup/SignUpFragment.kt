package com.yigitalasoy.stylestreetapp.ui.fragment.signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentSignUpBinding
import com.yigitalasoy.stylestreetapp.model.UserResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    @Inject lateinit var userViewModel: UserViewModel

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

        userViewModel.userLiveData.value = Resource.success(null)


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
                if(emailValidation && isBigChar && isNumber && isSevenChar){
                    //kayıt olma işlemi yapılacak
                    Log.e("KAYIT","KAYIT GEREKLİLİKLERİ SAĞLANDI")

                    userViewModel.userSignUp(getUser())

                } else {
                    this@SignUpFragment.toast("Please enter your email and password according to the rules.")
                    Log.e("KAYIT","KAYIT GEREKLİLİKLERİ SAĞLANMADI isSevenChar:$isSevenChar emailValidation:$emailValidation isBigChar:$isBigChar isNumber:$isNumber")
                }
            }
        }

        observer()

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
            when(it.status){
                Status.SUCCESS -> {
                    Log.i("user success","")

                    if(it.data != null){
                        binding.textViewSignupError.hide()
                        binding.progressBarSignupLoading.hide()

                        Toast.makeText(context,"Succesfully registered", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                        userViewModel.userLiveData.value = Resource.success(null)

                    }
                }
                Status.ERROR -> {
                    Log.i("user error",it.message.toString())

                    binding.textViewSignupError.show()
                    binding.progressBarSignupLoading.hide()

                    this.toast(it.message.toString())

                }
                Status.LOADING -> {
                    Log.i("user loading","")
                    binding.progressBarSignupLoading.show()
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
        /*val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"*/

        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")
        binding.apply {
            //emailValidation = editTextEmail.text.toString().matches(emailRegex.toRegex())
            emailValidation = emailRegex.matches(editTextEmail.text.toString())

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