package com.yigitalasoy.stylestreetapp.ui.fragment.recoverpassword

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentRecoverPasswordBinding
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecoverPasswordFragment : Fragment() {

    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!
    var emailValidation: Boolean = false

    @Inject lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverPasswordBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            imageViewBack.setOnClickListener {
                findNavController().navigate(R.id.action_recoverPasswordFragment_to_loginFragment)
            }

            editTextEmail.doOnTextChanged { text, start, before, count ->
                checkEmailValidation()
            }

            buttonSendRecoverLink.setOnClickListener {
                if(emailValidation){
                    val email = editTextEmail.text.toString()

                    email.let {
                        userViewModel.sendResetPasswordLink(it,this@RecoverPasswordFragment)
                        this@RecoverPasswordFragment.toast("Recovery password link successfully send.")
                    }
                } else {
                    this@RecoverPasswordFragment.toast("Please enter your email and password according to the rules.")
                }

            }
        }
    }

    fun checkEmailValidation() {
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



}