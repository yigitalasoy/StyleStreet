package com.yigitalasoy.stylestreetapp.ui.activity.productdetail

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.ActivityProductDetailBinding
import com.yigitalasoy.stylestreetapp.model.ProductColorResponse
import com.yigitalasoy.stylestreetapp.model.ProductSizeResponse
import com.yigitalasoy.stylestreetapp.model.SubProductResponse
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    @Inject lateinit var productViewModel: ProductViewModel
    private lateinit var selectedSubProducts: ArrayList<SubProductResponse>
    private var subProductQuantity: Int = 1
    private val imageAdapter = ProductImageAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var selectedProductId = intent.getStringExtra("selectedProductId")
        var selectedSubProductId = intent.getStringExtra("selectedSubProductId")


        getSelectedSubProduct(selectedProductId, selectedSubProductId)


        selectedProductId?.let {
            selectedSubProducts = productViewModel.getProductByProductIdAndSizeId(selectedProductId,null)
        }


        var selectedProductSizeItems = ArrayList<ProductSizeResponse>()

        for (size in selectedSubProducts){
            selectedProductSizeItems.add(size.subProductSizeId)
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
                val coloritems = ArrayList<ProductColorResponse>()

                val itemSelected: ProductSizeResponse = parent?.getItemAtPosition(position) as ProductSizeResponse

                selectedSubProducts = productViewModel.getProductByProductIdAndSizeId(selectedProductId!!,itemSelected.productSizeId)

                for (color in selectedSubProducts){
                    coloritems.add(color.subProductColorId)
                }

                val colorAdapter = ProductColorAdapter(this@ProductDetailActivity,coloritems)
                binding.spinnerProductColor.setAdapter(colorAdapter)
                println("seçilen size name: ${itemSelected.productSizeName} \t renkleri: ${coloritems}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                this@ProductDetailActivity.toast("item seçilmedi")
            }
        }


        binding.spinnerProductColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected: ProductColorResponse = parent?.getItemAtPosition(position) as ProductColorResponse

                imageAdapter.updateCategoryList(selectedSubProducts.find { it.subProductColorId.colorId == itemSelected.colorId}?.subProductImageURL!!)



                binding.apply {
                    textViewSubProductPrice.text = selectedSubProducts.find { it.subProductColorId.colorId ==  itemSelected.colorId}?.subProductPrice + " TL"
                    textViewSubProductBasketPrice.text = selectedSubProducts.find { it.subProductColorId.colorId ==  itemSelected.colorId}?.subProductPrice + " TL"

                    //imagelar değişecek
                }
                this@ProductDetailActivity.toast("tıklanan: ${itemSelected.colorName}")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                this@ProductDetailActivity.toast("item seçilmedi")
            }
        }

        binding.fabBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.apply {

            fabMinus.setOnClickListener {
                if(subProductQuantity == 1){
                    this@ProductDetailActivity.toast("The number of orders cannot be less than 1")
                } else {
                    subProductQuantity--
                    textViewSubProductQuantity.text = subProductQuantity.toString()
                }
            }

            fabPlus.setOnClickListener {
                subProductQuantity++
                textViewSubProductQuantity.text = subProductQuantity.toString()
            }


        }

    }

    private fun getSelectedSubProduct(
        selectedProductId: String?,
        selectedSubProductId: String?
    ) {
        var selectedSubProduct: SubProductResponse?
        selectedProductId?.let {
            selectedSubProductId?.let {
                selectedSubProduct =
                    productViewModel.getSubProductWithId(selectedProductId, selectedSubProductId)
                binding.apply {
                    textViewSubProductName.text = selectedSubProduct?.subProductName
                    textViewSubProductPrice.text = selectedSubProduct?.subProductPrice + " TL"
                    textViewSubProductBasketPrice.text = selectedSubProduct?.subProductPrice + " TL"
                }
            }
        }
    }
}