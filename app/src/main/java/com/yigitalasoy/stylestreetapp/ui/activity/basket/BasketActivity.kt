package com.yigitalasoy.stylestreetapp.ui.activity.basket

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivityBasketBinding
import com.yigitalasoy.stylestreetapp.model.SoldResponse
import com.yigitalasoy.stylestreetapp.ui.activity.address.AddressActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.SoldViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class BasketActivity : AppCompatActivity() {

    private var _binding: ActivityBasketBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var addressViewModel: AddressViewModel
    @Inject lateinit var soldViewModel: SoldViewModel

    private lateinit var basketProductAdapter: BasketProductAdapter

    private var SUB_TOTAL = 0
    private var SHIPPING_COST = 0
    private var BASKET_TOTAL = 0
    var selectedAddressId: String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding?.apply {
            selectedAddressId = intent.getStringExtra("selectedAddressId").toString()
            println("selectedAddressId:$selectedAddressId ${selectedAddressId.isEmpty()}   ${selectedAddressId.isNotEmpty()}")
            if (selectedAddressId != "null") {
                textViewAddressName.show()
                textViewAddressDetail.show()
                textViewBasketAddAddress.hide()

                val selectedAddress =
                    addressViewModel.addressLiveData.value?.data?.find { it.addressId == selectedAddressId }

                textViewAddressName.text = selectedAddress?.addressHeader
                textViewAddressDetail.text = selectedAddress?.addressDetail

            } else {
                textViewAddressName.hide()
                textViewAddressDetail.hide()
                textViewBasketAddAddress.show()
            }

        }

        //basketViewModel.getBasketData(userViewModel.userLiveData.value!!.data!!.id!!)

        basketProductAdapter = BasketProductAdapter(this,
            arrayListOf(), null, object : ItemClickListener{
            override fun onItemClick(Item: Any) {
                Item as Map<String,String>
                val subProductId = Item["subProductId"]!!
                val type = Item["type"]!!
                val position: Int = Item["position"]!!.toInt()

                if(type == "remove"){
                    basketViewModel.basketRemoveProduct(userViewModel.userLiveData.value?.data?.id!!,subProductId,type,position)
                } else {
                    basketViewModel.basketProductChangeQuantity(userViewModel.userLiveData.value?.data?.id!!,subProductId,type,position)
                }

            }
        })

        binding.recyclerViewBasketProduct.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL,false)
            adapter = basketProductAdapter
        }



        binding.fabBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.textViewShippingCost.text = SHIPPING_COST.toString()

        binding.buttonShippingAddress.setOnClickListener {
            val addressActivity = Intent(this,AddressActivity::class.java)
            this.startActivity(addressActivity)
            this.finish()
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if(basketViewModel.basketLiveData.value?.data?.basketProducts?.isNotEmpty() ?: false){
                if(selectedAddressId != "null"){
                    //sepetteki veriler sold tablosuna eklenecek
                    //sepetteki ürünlerde sold detail tablosuna eklenecek

                    //
                    soldViewModel.getUserSold(userViewModel.userLiveData.value?.data?.id!!)
                    soldViewModel.getUserSoldDetail(userViewModel.userLiveData.value?.data?.id!!)

                    soldViewModel.addUserSold(
                        userViewModel.userLiveData.value?.data?.id!!,
                        SoldResponse(
                            null,
                            userViewModel.userLiveData.value?.data?.id,
                            binding.textViewBasketTotal.text.toString(),
                            Instant.now().epochSecond.toString(),
                            "Preparing order",
                            selectedAddressId,
                            Instant.now().epochSecond.toString(),
                            false),
                        basketViewModel.basketLiveData.value?.data?.basketProducts!!)


                } else {
                    this.toast("Please select shipping address")
                }
            } else {
                this.toast("Please add product to basket")
            }

        }

        binding.buttonStartShopping.setOnClickListener {
            super.onBackPressed()
        }

        observer()


    }

    fun observer(){
        basketViewModel.basketLiveData.observe(this){
            println("basketViewModel.basketLiveData.observe çalıştı")

            if(it != null){
                when(it.status){
                    Status.SUCCESS -> {
                        Log.i("basket data success","")
                        binding.progressBarLoading.hide()
                        binding.textViewError.hide()
                        if(it.data != null) {
                            binding.constraintLayoutEmptyList.hide()
                            binding.recyclerViewBasketProduct.show()
                            println("basketViewModel.basketLiveData.observe null gelmedi")
                            println("basket datası geldi: $it.data")
                            basketViewModel.getBasketProducts(productViewModel.allProductLiveData.value?.data!!)
                        } else {
                            binding.constraintLayoutEmptyList.show()
                            binding.recyclerViewBasketProduct.visibility = View.INVISIBLE

                        }
                    }
                    Status.ERROR -> {
                        this.toast("ERROR BASKET DATA: ${it.message}")
                        Log.i("basket data error",it.message.toString())

                        binding.textViewError.show()
                        binding.progressBarLoading.hide()
                    }
                    Status.LOADING -> {
                        Log.i("basket data loading","")
                        binding.progressBarLoading.show()
                    }
                }
            }


        }


        basketViewModel.basketSubProductsLiveData.observe(this){
            println("basketViewModel.basketSubProductsLiveData.observe çalıştı")

            if(it != null){
                when(it.status){
                    Status.SUCCESS -> {
                        Log.i("basket subproducts success","")

                        binding.progressBarLoading.hide()
                        binding.textViewError.hide()

                        if(it.data != null){
                            binding.recyclerViewBasketProduct.show()
                            binding.constraintLayoutEmptyList.hide()

                            SUB_TOTAL = 0
                            BASKET_TOTAL = 0

                            if(it.data.size != 0){
                                SHIPPING_COST = 50
                            } else {
                                SHIPPING_COST = 0
                            }

                            if(it.data.size != 0){


                                println("basketViewModel.basketSubProductsLiveData.observe null gelmedi")

                                basketProductAdapter.updateBasketSubProductList(it.data,basketViewModel.basketLiveData.value?.data!!)

                                it.data.forEach {subProduct ->
                                    SUB_TOTAL += (subProduct.subProductPrice!!.toInt()) * (basketViewModel.basketLiveData.value?.data?.basketProducts?.find { it.subProductId == subProduct.subProductId }!!.quantity!!.toInt())
                                    BASKET_TOTAL = SUB_TOTAL + SHIPPING_COST
                                }
                            } else {
                                binding.recyclerViewBasketProduct.visibility = View.INVISIBLE
                                binding.constraintLayoutEmptyList.show()
                            }

                            binding.textViewSubTotal.text = SUB_TOTAL.toString()
                            binding.textViewBasketTotal.text = BASKET_TOTAL.toString()
                            binding.textViewShippingCost.text = SHIPPING_COST.toString()

                        }

                    }
                    Status.ERROR -> {
                        this.toast("ERROR BASKET PRODUCTS DATA: ${it.message}")
                        Log.i("basket subproducts error",it.message.toString())

                        binding.textViewError.show()
                        binding.progressBarLoading.hide()

                    }
                    Status.LOADING -> {
                        Log.i("basket subproducts loading","")
                        binding.progressBarLoading.show()
                    }
                }
            }
        }

        soldViewModel.soldLiveData.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBarLoading.hide()
                    binding.textViewError.hide()
                    if(it.data != null){
                        println("USER SOLD BAŞARILI: ${it.data}")
                    }
                }
                Status.ERROR -> {
                    binding.progressBarLoading.hide()
                    binding.textViewError.show()
                    println("USER SOLD ERROR: ${it.message}")
                }
                Status.LOADING -> {
                    binding.progressBarLoading.show()
                    println("USER SOLD LOADİNG")
                }
            }

        }


        soldViewModel.soldDetailLiveData.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBarLoading.hide()
                    binding.textViewError.hide()

                    if(it.data != null){
                        println("USER SOLD DETAİL BAŞARILI: ${it.data}")
                    }
                }
                Status.ERROR -> {
                    binding.progressBarLoading.hide()
                    binding.textViewError.show()
                    println("USER SOLD DETAİL ERROR: ${it.message}")
                }
                Status.LOADING -> {
                    println("USER SOLD DETAİL LOADİNG")
                    binding.progressBarLoading.show()
                }
            }
        }

        soldViewModel.addSoldState.observe(this){

            when(it.status){
                Status.SUCCESS -> {
                    if(it.data == true){

                        this.toast("Your order has been successfully received")
                        this.finish()

                        println("add sold state success: ${it.data}")

                        SUB_TOTAL = 0
                        BASKET_TOTAL = 0
                        SHIPPING_COST = 0


                        binding.apply {
                            textViewAddressName.hide()
                            textViewAddressDetail.hide()
                            textViewBasketAddAddress.show()

                            textViewSubTotal.text = SUB_TOTAL.toString()
                            textViewBasketTotal.text = BASKET_TOTAL.toString()
                            textViewShippingCost.text = SHIPPING_COST.toString()
                        }

                        selectedAddressId = "null"

                        soldViewModel.addSoldState.value = Resource.success(null)
                        //basketViewModel.basketLiveData.value = Resource.success(null)
                        //basketViewModel.basketSubProductsLiveData.value = Resource.success(null)

                        basketViewModel.removeAllProducts(userViewModel.userLiveData.value?.data?.id!!,basketViewModel.basketLiveData.value?.data!!)

                        //basketteki sadece ürünler gidecek.

                    }
                }
                Status.ERROR -> {
                    println("add sold state error: ${it.message}")
                }
                Status.LOADING -> {
                    println("add sold state loading")
                }
            }
        }
    }
}