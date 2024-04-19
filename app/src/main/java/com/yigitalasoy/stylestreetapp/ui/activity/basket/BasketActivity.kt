package com.yigitalasoy.stylestreetapp.ui.activity.basket

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivityBasketBinding
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BasketActivity : AppCompatActivity() {

    private var _binding: ActivityBasketBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productViewModel: ProductViewModel

    private lateinit var basketProductAdapter: BasketProductAdapter

    private var SUB_TOTAL = 0
    private var SHIPPING_COST = 50
    private var BASKET_TOTAL = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        observer()


    }

    fun observer(){
        basketViewModel.basketLiveData.observe(this){
            println("basketViewModel.basketLiveData.observe çalıştı")

            if(it != null){
                when(it.status){
                    Status.SUCCESS -> {
                        Log.i("basket data success","")

                        if(it.data != null) {
                            binding.progressBarLoading.hide()
                            binding.textViewError.hide()

                            println("basketViewModel.basketLiveData.observe null gelmedi")
                            println("basket datası geldi: $it.data")
                            basketViewModel.getBasketProducts(productViewModel.allProductLiveData.value?.data!!)
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

                        if(it.data != null){
                            binding.progressBarLoading.hide()
                            binding.textViewError.hide()


                            SUB_TOTAL = 0
                            BASKET_TOTAL = 0
                            if(it.data?.size != 0){
                                SHIPPING_COST = 50
                            } else {
                                SHIPPING_COST = 0
                            }
                            println("basketViewModel.basketSubProductsLiveData.observe null gelmedi")

                            basketProductAdapter.updateBasketSubProductList(it.data,basketViewModel.basketLiveData.value?.data!!)

                            it.data.forEach {subProduct ->
                                SUB_TOTAL += (subProduct.subProductPrice!!.toInt()) * (basketViewModel.basketLiveData.value?.data?.basketProducts?.find { it.subProductId == subProduct.subProductId }!!.quantity!!.toInt())
                                BASKET_TOTAL = SUB_TOTAL + SHIPPING_COST
                            }

                            binding.textViewSubTotal.setText(SUB_TOTAL.toString())
                            binding.textViewBasketTotal.setText(BASKET_TOTAL.toString())
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
    }
}