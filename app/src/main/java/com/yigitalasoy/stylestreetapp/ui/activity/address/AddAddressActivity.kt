package com.yigitalasoy.stylestreetapp.ui.activity.address

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yigitalasoy.stylestreetapp.databinding.ActivityAddAddressBinding
import com.yigitalasoy.stylestreetapp.model.AddressResponse
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddAddressBinding

    @Inject lateinit var addressViewModel: AddressViewModel
    @Inject lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            fabBack.setOnClickListener {
                super.onBackPressed()
                this@AddAddressActivity.finish()
            }

            buttonSaveAddress.setOnClickListener {

                val sameHeaderName = addressViewModel.addressLiveData.value?.data?.find { it.addressHeader == editTextAddressHeader.text.toString() }

                if(sameHeaderName != null){
                    this@AddAddressActivity.toast("Already have the same address header. Please change address header.")
                } else {
                    addressViewModel.addAddress(AddressResponse(
                        userViewModel.userLiveData.value?.data?.id,
                        null,
                        editTextAddressDetail.text.toString(),
                        editTextAddressHeader.text.toString(),
                        editTextAddressDistrict.text.toString(),
                        editTextAddressProvince.text.toString(),
                        editTextAddressTel.text.toString(),
                        editTextAddressName.text.toString(),
                        editTextAddressSurname.text.toString()
                    ))
                }

            }
        }

        observe()

    }

    fun observe(){
        addressViewModel.newAddressLiveData.observe(this){newAddress ->

            when(newAddress.status){
                Status.SUCCESS -> {

                    if(newAddress.data != null){
                        this.toast("Address succesfully added: ${newAddress.data.addressHeader}")
                        addressViewModel.addressLiveData.value?.data?.add(newAddress.data)

                        addressViewModel.addressLiveData.value = Resource.success(addressViewModel.addressLiveData.value?.data)


                        this.finish()

                    }

                }
                Status.ERROR -> {
                    println("new address error: ${newAddress.message.toString()}")
                }
                Status.LOADING -> {
                    println("new address loading")
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("addaddress destroyed")
    }

}