package com.yigitalasoy.stylestreetapp.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yigitalasoy.stylestreetapp.databinding.FragmentProfileBinding
import com.yigitalasoy.stylestreetapp.ui.activity.address.AddressActivity
import com.yigitalasoy.stylestreetapp.ui.activity.main.MainActivity
import com.yigitalasoy.stylestreetapp.util.downloadImage
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    @Inject lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(!(userViewModel.userLiveData.value?.data?.userImageURL.equals(""))){
            binding.imageViewUserImage.downloadImage(userViewModel.userLiveData.value?.data?.userImageURL)
        }

        binding.apply {

            textViewUserNameAndSurname.text = userViewModel.userLiveData.value?.data?.name + userViewModel.userLiveData.value?.data?.surname
            textViewUserEmail.text = userViewModel.userLiveData.value?.data?.email
            textViewUserTel.text = userViewModel.userLiveData.value?.data?.telephone ?: "-"


            buttonUserAddress.setOnClickListener {
                val addressIntent = Intent(requireContext(),AddressActivity::class.java)
                activity?.startActivity(addressIntent)

            }

            buttonUserOrders.setOnClickListener {

            }

            buttonUserWishList.setOnClickListener {
                (activity as MainActivity?)?.wishListButton()
            }

        }



    }
}