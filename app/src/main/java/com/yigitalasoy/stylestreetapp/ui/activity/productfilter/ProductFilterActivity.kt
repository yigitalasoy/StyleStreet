package com.yigitalasoy.stylestreetapp.ui.activity.productfilter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yigitalasoy.stylestreetapp.databinding.ActivityProductFilterBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.ui.activity.productdetail.ProductDetailActivity
import com.yigitalasoy.stylestreetapp.ui.fragment.main.ProductAdapter
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductFilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityProductFilterBinding

    var gelenCategoryId: String? = null
    @Inject lateinit var productViewModel: ProductViewModel
    lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gelenCategoryId = intent.getStringExtra("categoryId")

        productAdapter = ProductAdapter(arrayListOf(),object: ItemClickListener {
            override fun onItemClick(position: Any) {
                position as ProductResponse

                val productDetailActivity = Intent(this@ProductFilterActivity, ProductDetailActivity::class.java)
                productDetailActivity.putExtra("selectedProductId",productViewModel.allProductLiveData.value!!.data?.find { it.productId == position.productId }!!.productId)
                productDetailActivity.putExtra("selectedSubProductId",
                    productViewModel.allProductLiveData.value!!.data?.find { it.productId == position.productId }?.allProducts?.get(0)?.subProductId.toString()
                )
                startActivity(productDetailActivity)
            }
        })

        binding.apply {
            recyclerViewAllProducts.apply {
                adapter = productAdapter
                layoutManager = GridLayoutManager(context,2)
            }

            fabBack2.setOnClickListener {
                this@ProductFilterActivity.finish()
            }
        }

        if(gelenCategoryId != null){
            println("gelen: $gelenCategoryId")
            binding.apply {

                productViewModel.allProductLiveData.value?.data?.filter { it.categoryId?.categoryId == gelenCategoryId }?.let {
                    if(it.isNotEmpty()){
                        productAdapter.updateProductList(it)
                        textViewEmptyList.hide()
                        recyclerViewAllProducts.show()
                    } else {
                        textViewEmptyList.show()
                        recyclerViewAllProducts.hide()
                    }
                }
            }

        } else {
            println("gelen category id null geldi")
        }
    }
}