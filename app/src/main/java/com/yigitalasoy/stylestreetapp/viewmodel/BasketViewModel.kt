package com.yigitalasoy.stylestreetapp.viewmodel

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.BasketResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.repository.BasketRepository
import com.yigitalasoy.stylestreetapp.ui.activity.basket.BasketActivity
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.downloadImage
import com.yigitalasoy.stylestreetapp.util.toast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class BasketViewModel @Inject constructor(val basketRepository: BasketRepository): ViewModel() {

    val basketLiveData = MutableLiveData<Resource<BasketResponse>>()
    val basketSubProductsLiveData = MutableLiveData<Resource<ArrayList<SubProductResponse>>>()
    //val basketLoading = MutableLiveData<Resource<Boolean>>()
    //val basketError = MutableLiveData<Resource<Boolean>>()


    private var job : Job? = null
    private var productQuantityTask = false
    private var productRemoveTask = false

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
        basketLiveData.value = Resource.error(throwable.message.toString(),null)
    }

    fun getBasketData(userId: String){

        basketLiveData.value = Resource.loading(null)


        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = basketRepository.getUserBasket(userId)
            withContext(Dispatchers.Main){
                when(response.status){
                    Status.SUCCESS -> {
                        basketLiveData.value = Resource.success(response.data)
                    }
                    Status.ERROR -> {
                        basketLiveData.value = Resource.error(response.message.toString(),null)
                    }
                    Status.LOADING -> {
                        basketLiveData.value = Resource.loading(null)
                    }
                }
            }
        }
    }

    fun getBasketProducts(products: ArrayList<ProductResponse>){

        products.let {

            val productsYeni = basketLiveData.value?.data?.basketProducts!!
            println("products yeni: $productsYeni")
            val subproductsYeni = ArrayList<SubProductResponse>()
            val productByIdMap = products.associateBy { it.productId }

            for (urun in productsYeni){
                val z = productByIdMap[urun.productId.toString()]?.allProducts?.find { it.subProductId == urun.subProductId }
                z?.let {
                    subproductsYeni.add(z)
                }
            }

            basketSubProductsLiveData.value = Resource.success(subproductsYeni)
        }
    }

    fun basketProductChangeQuantity(userId: String,subProductId: String, type: String, position: Int){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if(!productQuantityTask){
                productQuantityTask = true
                var state = basketRepository.productQuantityChange(userId,subProductId,type,position)

                withContext(Dispatchers.Main){

                    var newLiveData = MutableLiveData<Resource<BasketResponse>>()

                    newLiveData.value = basketLiveData.value

                    if(state && type.equals("increase")){
                        newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity = (newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity!!) + 1
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                    } else if(state && type.equals("decrease")){
                        newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity = (newLiveData.value?.data?.basketProducts?.find { it.subProductId == subProductId }?.quantity!!) - 1
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                    }

                    productQuantityTask = false
                }
            } else {
                println("quantity change task devam ettiği için girilmedi!")
            }

        }
    }

    fun basketRemoveProduct(userId: String,subProductId: String, type: String, position: Int){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if(!productRemoveTask){
                productRemoveTask=true

                var state = basketRepository.productRemove(userId,subProductId,type,position)

                withContext(Dispatchers.Main){

                    var newLiveData = MutableLiveData<Resource<BasketResponse>>()

                    newLiveData.value = basketLiveData.value
                    if(state){
                        println("newLiveDatavalue: ${newLiveData.value}")
                        newLiveData.value?.data?.basketProducts?.removeAt(position)
                        println("newLiveDatavalue sildikten sonra: ${newLiveData.value}")
                        basketLiveData.value = Resource.success(newLiveData.value!!.data)
                        println("basketLiveData sildikten sonra: ${basketLiveData.value}")

                    }

                    productRemoveTask = false
                }

            } else {
                println("remove task devam ettiği için girilmedi !")
            }
        }

    }

    fun addProductToBasket(userId: String,basketDetail: BasketDetailResponse, activity: Activity,selectedSubProductResponse: SubProductResponse){

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            var basketResponse = BasketResponse(null,userId, arrayListOf(basketDetail))

            var varmi = false
            var returnHashMap: HashMap<String,Any>? = null
            if((basketLiveData.value?.data?.basketProducts?.find { it.subProductId == basketDetail.subProductId }) != null){
                varmi = true
            } else {
                varmi = false
                returnHashMap = basketRepository.addProduct(userId,basketResponse)
            }


            withContext(Dispatchers.Main){
                if(varmi){
                    println("aynı ürün var")
                    activity.toast("You already have this product in your basket")
                } else {
                    if(returnHashMap!!["state"] as Boolean){
                        println("geri dönen id: ${returnHashMap["newBasketProductId"].toString()}")

                        basketDetail.basketDetailId = returnHashMap["newBasketProductId"].toString()
                        basketDetail.basketId = returnHashMap["basketId"].toString()


                        println("baskete eklenecek detay: $basketDetail")

                        println("basket:::: ${basketLiveData.value}")
                        basketLiveData.value?.data?.basketProducts?.add(basketDetail)
                        basketLiveData.value = Resource.success(basketLiveData.value?.data)
                        println("BASKET live data updated: ${basketLiveData.value?.data}")

                        showPopUpAlert(activity,selectedSubProductResponse)

                    } else {
                        println("basket ürün ekleme state false geldi")
                    }
                }
            }
        }
    }

    private fun showPopUpAlert(activity: Activity, selectedSubProductResponse: SubProductResponse) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.product_added_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window: Window = dialog.window!!
        val wlp = window.attributes

        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.setAttributes(wlp)

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            screenWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setLayout(layoutParams.width, layoutParams.height)

        val buttonGoToBasket: Button = dialog.findViewById(R.id.buttonPopUpBasket)
        val imageViewPopupImage: ImageView = dialog.findViewById(R.id.imageViewPopUpProductImage)
        val textViewPopupProductName: TextView = dialog.findViewById(R.id.textViewPopUpProductName)

        imageViewPopupImage.downloadImage(selectedSubProductResponse?.subProductImageURL!![0])
        textViewPopupProductName.text = selectedSubProductResponse?.subProductName

        buttonGoToBasket.setOnClickListener {
            dialog.cancel()
            val basketActivityIntent = Intent(activity, BasketActivity::class.java)
            activity.startActivity(basketActivityIntent)
            activity.finish()
        }

        dialog.show()


        Handler(Looper.getMainLooper()).postDelayed({
            if(dialog.isShowing){
                dialog.cancel()
            }
        }, 3000)

    }

    fun removeAllProducts(userId: String, basketData: BasketResponse){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            basketData.basketProducts = arrayListOf()

            val state = basketRepository.removeAllProducts(userId,basketData)

            withContext(Dispatchers.Main){

                if(state){
                    basketLiveData.value = Resource.success(null)
                    basketSubProductsLiveData.value = Resource.success(null)
                } else {
                    println("basket live data all products not removed, state is false")
                }
            }
        }
    }

}