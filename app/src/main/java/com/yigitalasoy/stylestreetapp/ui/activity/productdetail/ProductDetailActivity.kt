package com.yigitalasoy.stylestreetapp.ui.activity.productdetail

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ActivityProductDetailBinding
import com.yigitalasoy.stylestreetapp.model.BasketDetailResponse
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var wishListViewModel: WishListViewModel


    private lateinit var selectedSubProducts: ArrayList<SubProductResponse>
    private var subProductQuantity: Int = 1
    private val imageAdapter = ProductImageAdapter(arrayListOf())
    var selectedSubProduct: SubProductResponse? = null
    var coloritems = ArrayList<ProductColorResponse>()
    private var selectedProductId: String? = null
    private var selectedSubProductId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedProductId = intent.getStringExtra("selectedProductId")
        selectedSubProductId = intent.getStringExtra("selectedSubProductId")

        println("selected product ıd: $selectedProductId")
        println("selected subproduct ıd: $selectedSubProductId")

        getSelectedSubProduct(selectedProductId, selectedSubProductId)

        val resources: Resources = getResources()
        if(wishListViewModel.wishListLiveData.value?.data?.find { it.product_Id == selectedProductId } != null){
            //binding.fabLike.setImageResource(R.drawable.heart_filled)
            binding.fabLike.setImageDrawable(resources.getDrawable(R.drawable.heart_filled))
        } else {
            //binding.fabLike.setImageResource(R.drawable.heart)
            binding.fabLike.setImageDrawable(resources.getDrawable(R.drawable.heart))

        }


        selectedProductId?.let {
            selectedSubProducts = productViewModel.getProductByProductIdAndSizeId(selectedProductId!!,null)
        }

        var selectedProductSizeItems = ArrayList<ProductSizeResponse>()

        for (size in selectedSubProducts){
            selectedProductSizeItems.add(size.subProductSizeId!!)
        }

        val sizeAdapter = ProductSizeAdapter(this,selectedProductSizeItems.distinctBy { it.productSizeId })
        binding.spinnerProductSize.setAdapter(sizeAdapter)



        binding.recyclerViewProductDetailImage.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
            adapter = imageAdapter
        }



        binding.spinnerProductSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                coloritems.clear()
                val itemSelected: ProductSizeResponse = parent?.getItemAtPosition(position) as ProductSizeResponse

                println("seçilen spinner productsize: ${binding.spinnerProductSize.selectedItem}")

                selectedSubProducts = productViewModel.getProductByProductIdAndSizeId(selectedProductId!!,itemSelected.productSizeId)

                for (color in selectedSubProducts){
                    coloritems.add(color.subProductColorId!!)
                }

                val colorAdapter = ProductColorAdapter(this@ProductDetailActivity,coloritems)
                binding.spinnerProductColor.setAdapter(colorAdapter)
                binding.spinnerProductColor.setSelection(coloritems.indexOf(selectedSubProduct?.subProductColorId))

                println("seçilen size name: ${itemSelected.productSizeName} \t renkleri: ${coloritems}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //this@ProductDetailActivity.toast("item seçilmedi")
            }
        }
        binding.spinnerProductSize.setSelection(selectedProductSizeItems.distinctBy { it.productSizeId }.indexOf(selectedSubProduct?.subProductSizeId))

        binding.spinnerProductColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected: ProductColorResponse = parent?.getItemAtPosition(position) as ProductColorResponse

                if((selectedSubProducts.find { it.subProductColorId?.colorId == itemSelected.colorId}?.subProductImageURL) != null){
                    imageAdapter.updateCategoryList(selectedSubProducts.find { it.subProductColorId?.colorId == itemSelected.colorId}?.subProductImageURL!!)
                }

                selectedSubProduct = selectedSubProducts.find { it.subProductColorId?.colorId == itemSelected.colorId}
                println("selected sub product değişti: $selectedSubProduct")


                binding.apply {
                    textViewSubProductPrice.text = selectedSubProducts.find { it.subProductColorId?.colorId ==  itemSelected.colorId}?.subProductPrice
                    textViewSubProductBasketPrice.text = selectedSubProducts.find { it.subProductColorId?.colorId ==  itemSelected.colorId}?.subProductPrice
                    textViewSubProductName.text = selectedSubProducts.find { it.subProductColorId?.colorId ==  itemSelected.colorId}?.subProductName
                    selectedSubProductId = selectedSubProducts.find { it.subProductColorId?.colorId ==  itemSelected.colorId}?.subProductId
                }
                //this@ProductDetailActivity.toast("tıklanan: ${itemSelected.colorName}")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //this@ProductDetailActivity.toast("item seçilmedi")
            }
        }

        binding.fabBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.apply {

            fabMinus.setOnClickListener {
                if(subProductQuantity == 1){
                    this@ProductDetailActivity.toast("The number of orders cannot be less than 1")
                    fabMinus.isClickable = false
                } else {
                    fabPlus.isClickable = true
                    subProductQuantity--
                    textViewSubProductQuantity.text = subProductQuantity.toString()
                    textViewSubProductBasketPrice.text = (subProductQuantity * textViewSubProductPrice.text.toString().toInt()).toString()
                }
            }

            fabPlus.setOnClickListener {
                if(subProductQuantity==9){
                    this@ProductDetailActivity.toast("The number of orders cannot be more than 9")
                    fabPlus.isClickable = false
                } else {
                    fabMinus.isClickable = true
                    subProductQuantity++
                    textViewSubProductQuantity.text = subProductQuantity.toString()
                    textViewSubProductBasketPrice.text = (subProductQuantity * textViewSubProductPrice.text.toString().toInt()).toString()
                }
            }

            buttonAddToBasket.setOnClickListener {

                basketViewModel.addProductToBasket(userViewModel.userLiveData.value?.data?.id!!, BasketDetailResponse(
                    null,
                    basketViewModel.basketLiveData.value?.data?.basketId,
                    selectedSubProductId,
                    selectedProductId,
                    textViewSubProductQuantity.text.toString().toInt(),
                    textViewSubProductPrice.text.toString().toInt()),this@ProductDetailActivity, selectedSubProduct!!)
            }

            fabLike.setOnClickListener {

                if (fabLike.drawable.constantState == ContextCompat.getDrawable(this@ProductDetailActivity, R.drawable.heart)?.constantState) {
                    wishListViewModel.addProductToWishList(userViewModel.userLiveData.value?.data?.id!!,selectedProductId!!)
                    binding.fabLike.setImageResource(R.drawable.heart_filled)
                } else {
                    wishListViewModel.removeProductToWishList(wishListViewModel.wishListLiveData.value?.data?.find { it.product_Id == selectedProductId }?.wish_Id!!)
                    binding.fabLike.setImageResource(R.drawable.heart)
                }

            }

            println("set selection: ${selectedProductSizeItems.distinctBy { it.productSizeId }.indexOf(selectedSubProduct?.subProductSizeId)}")
            println("set selection: ${coloritems.indexOf(selectedSubProduct?.subProductColorId)}")
        }


    }

    private fun getSelectedSubProduct(
        selectedProductId: String?,
        selectedSubProductId: String?
    ) {
        selectedProductId?.let {
            selectedSubProductId?.let {
                selectedSubProduct =
                    productViewModel.getSubProductWithId(selectedProductId, selectedSubProductId)
                binding.apply {
                    textViewSubProductName.text = selectedSubProduct?.subProductName
                    textViewSubProductPrice.text = selectedSubProduct?.subProductPrice
                    textViewSubProductBasketPrice.text = selectedSubProduct?.subProductPrice
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("detail activity destroyed")
    }
}