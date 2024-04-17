package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentMainBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.ui.activity.productdetail.ProductDetailActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _mainFragmentBinding: FragmentMainBinding? = null
    private val mainFragmentBinding get() = _mainFragmentBinding!!

    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var categoryViewModel: CategoryViewModel
    @Inject lateinit var productSizeViewModel: ProductSizeViewModel
    @Inject lateinit var productColorViewModel: ProductColorViewModel
    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var wishListViewModel: WishListViewModel


    private val categoryAdapter = CategoryAdapter(arrayListOf())
    private lateinit var newInProductAdapter: ProductAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mainFragmentBinding = FragmentMainBinding.inflate(inflater,container,false)
        val view = mainFragmentBinding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val searchAdapter = SearchAdapter(requireContext(), productViewModel.productLiveData.value?.data!!)

        newInProductAdapter = ProductAdapter(arrayListOf(),object: ItemClickListener{
            override fun onItemClick(position: Any) {

                //this@MainFragment.toast(productViewModel.newInProductLiveData.value!!.data?.get(position as Int)!!.productName)
                val productDetailActivity = Intent(activity, ProductDetailActivity::class.java)
                productDetailActivity.putExtra("selectedProductId",productViewModel.newInProductLiveData.value!!.data?.get(position as Int)!!.productId)
                productDetailActivity.putExtra("selectedSubProductId",
                    productViewModel.newInProductLiveData.value!!.data?.get(position as Int)!!.allProducts?.get(0)?.subProductId
                )
                startActivity(productDetailActivity)


            }
        })

        //productViewModel.getNewInProduct()




        val istanbulDateTime = LocalDateTime.now(ZoneId.of("Europe/Istanbul"))
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedDateTime = istanbulDateTime.format(formatter)
        println("güncel saat: $formattedDateTime")


        mainFragmentBinding.reyclerViewCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = categoryAdapter
        }

        mainFragmentBinding.recyclerViewNewIn.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = newInProductAdapter
        }

        if(categoryViewModel.categoryLiveData.value?.data == null ||
            productColorViewModel.productColorLiveData.value?.data == null ||
            productSizeViewModel.productSizeLiveData.value?.data == null ||
            basketViewModel.basketLiveData.value?.data == null ||
            wishListViewModel.wishListLiveData.value?.data == null){

            categoryViewModel.getAllCategories()
            productColorViewModel.getAllProductColors()
            productSizeViewModel.getAllProductSize()
            basketViewModel.getBasketData(userViewModel.userLiveData.value!!.data!!.id!!)
            wishListViewModel.getWishList(userViewModel.userLiveData.value?.data?.id!!)
        }


        mainFragmentBinding.buttonCikisYap.setOnClickListener {

            //Firebase.auth.signOut()

            userViewModel.signOut(requireContext())

            productViewModel.allProductLiveData.value = Resource.success(null)
            productViewModel.newInProductLiveData.value = Resource.success(null)
            categoryViewModel.categoryLiveData.value = Resource.success(null)
            productSizeViewModel.productSizeLiveData.value = Resource.success(null)
            productColorViewModel.productColorLiveData.value = Resource.success(null)
            basketViewModel.basketLiveData.value = Resource.success(null)
            basketViewModel.basketSubProductsLiveData.value = Resource.success(null)
            wishListViewModel.wishListLiveData.value = Resource.success(null)

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleServerClientId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(it.context,gso)
            googleSignInClient.signOut().addOnCompleteListener {
                Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
            }

        }

        observer()

    }

    private fun observer() {
        categoryViewModel.categoryLoading.observe(viewLifecycleOwner){
            if(it.data!!){
                mainFragmentBinding.progressBarFragmentMain.show()
                mainFragmentBinding.scrollViewFragmentMain.hide()

            } else {
                mainFragmentBinding.progressBarFragmentMain.hide()
                mainFragmentBinding.scrollViewFragmentMain.show()
            }
        }

        userViewModel.userLiveData.observe(viewLifecycleOwner){ user ->
            if(user.data == null){
                val loginActivity = Intent(activity, LoginActivity::class.java)
                startActivity(loginActivity)
                activity?.finish()
            }
        }

        categoryViewModel.categoryLiveData.observe(viewLifecycleOwner){ list ->
            if(list.data != null) {
                list?.let {
                    productViewModel.updateProductCategories(it.data!!)
                    categoryAdapter.updateCategoryList(it.data)
                }
            }
        }

        productViewModel.allProductLiveData.observe(viewLifecycleOwner){ products ->
            if(products.data != null){

                products?.let {
                    println("product observe çalıştı")

                    /*val searchAdapter = TestAdapter(requireContext(), products.data!!)

                    mainFragmentBinding.editTextSearch.apply {
                        setAdapter(searchAdapter)
                    }

                    productAdapter.updateProductList(it.data)*/



                    val searchAdapter = SearchAdapter(requireContext(), products.data!!, object : ItemClickListener{
                        override fun onItemClick(Item: Any) {
                            mainFragmentBinding.editTextSearch.text.clear()
                            Item as ProductResponse
                            //this@MainFragment.toast("tiklanan product id: ${Item.productId}")
                            val productDetailActivity = Intent(activity, ProductDetailActivity::class.java)
                            productDetailActivity.putExtra("selectedProductId", Item.productId)
                            productDetailActivity.putExtra("selectedSubProductId",
                                Item.allProducts?.get(0)?.subProductId
                            )
                            startActivity(productDetailActivity)


                        }
                    })

                    mainFragmentBinding.editTextSearch.apply {
                        setAdapter(searchAdapter)
                    }
                }
            }
        }

        productViewModel.newInProductLiveData.observe(viewLifecycleOwner){
            if(it.data != null){
                it?.let {
                    newInProductAdapter.updateProductList(it.data!!)
                }
            }

        }

        productColorViewModel.productColorLiveData.observe(viewLifecycleOwner){ colors ->
            if(colors.data != null) {
                colors?.let {
                    productViewModel.updateProductColorName(it.data!!)
                }
            }
        }

        productSizeViewModel.productSizeLiveData.observe(viewLifecycleOwner){ sizeList ->
            if(sizeList.data != null) {
                sizeList?.let {
                    productViewModel.updateProductSizeName(it.data!!)
                }
            }
        }

    }
}