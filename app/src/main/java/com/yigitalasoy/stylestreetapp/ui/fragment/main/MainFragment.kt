package com.yigitalasoy.stylestreetapp.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentMainBinding
import com.yigitalasoy.stylestreetapp.model.CategoryResponse
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.ui.activity.productdetail.ProductDetailActivity
import com.yigitalasoy.stylestreetapp.ui.activity.productfilter.ProductFilterActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.downloadImage
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.AddressViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
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
    @Inject lateinit var addressViewModel: AddressViewModel


    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var newInProductAdapter: ProductAdapter
    private lateinit var allProductAdapter: ProductAdapter



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
            override fun onItemClick(selectedItem: Any) {
                selectedItem as ProductResponse

                val productDetailActivity = Intent(activity, ProductDetailActivity::class.java)
                productDetailActivity.putExtra("selectedProductId",productViewModel.newInProductLiveData.value!!.data?.find { it.productId == selectedItem.productId }!!.productId)
                productDetailActivity.putExtra("selectedSubProductId",
                            productViewModel.newInProductLiveData.value!!.data?.find { it.productId == selectedItem.productId }?.allProducts?.get(0)?.subProductId.toString()
                )
                startActivity(productDetailActivity)
            }
        })

        allProductAdapter = ProductAdapter(arrayListOf(),object: ItemClickListener{
            override fun onItemClick(selectedItem: Any) {
                selectedItem as ProductResponse

                val productDetailActivity = Intent(activity, ProductDetailActivity::class.java)
                productDetailActivity.putExtra("selectedProductId",productViewModel.allProductLiveData.value!!.data?.find { it.productId == selectedItem.productId }!!.productId)
                productDetailActivity.putExtra("selectedSubProductId",
                    productViewModel.allProductLiveData.value!!.data?.find { it.productId == selectedItem.productId }?.allProducts?.get(0)?.subProductId.toString()
                )
                startActivity(productDetailActivity)
            }
        })

        categoryAdapter = CategoryAdapter(arrayListOf(), object : ItemClickListener{
            override fun onItemClick(Item: Any) {
                val productFilterActivity = Intent(this@MainFragment.context,ProductFilterActivity::class.java)
                productFilterActivity.putExtra("categoryId",(Item as CategoryResponse).categoryId)
                this@MainFragment.startActivity(productFilterActivity)
            }
        })

        //productViewModel.getNewInProduct()
        addressViewModel.getAddressData(userViewModel.userLiveData.value?.data?.id!!)



        val istanbulDateTime = LocalDateTime.now(ZoneId.of("Europe/Istanbul"))
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedDateTime = istanbulDateTime.format(formatter)

        println("güncel saat: $formattedDateTime epoch: ${Instant.now().epochSecond}")


        mainFragmentBinding.reyclerViewCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = categoryAdapter
        }

        mainFragmentBinding.recyclerViewNewIn.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = newInProductAdapter
        }

        mainFragmentBinding.recyclerViewAllProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = allProductAdapter
        }

        if(!(userViewModel.userLiveData.value?.data?.userImageURL.equals(""))){
            mainFragmentBinding.imageView3.downloadImage()
        }

        if(categoryViewModel.categoryLiveData.value?.data == null ||
            productColorViewModel.productColorLiveData.value?.data == null ||
            productSizeViewModel.productSizeLiveData.value?.data == null ||
            basketViewModel.basketLiveData.value?.data == null ||
            wishListViewModel.wishListLiveData.value?.data == null){

            categoryViewModel.getAllCategories()
            productColorViewModel.getAllProductColors()
            productSizeViewModel.getAllProductSize()
            basketViewModel.getBasketData(userViewModel.userLiveData.value?.data?.id.toString())
            wishListViewModel.getWishList(userViewModel.userLiveData.value?.data?.id.toString())
        }


        mainFragmentBinding.buttonCikisYap.setOnClickListener {

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
                Log.i("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
            }

        }

        observer()

    }

    private fun observer() {
        userViewModel.userLiveData.observe(viewLifecycleOwner){ user ->

            when(user.status){
                Status.SUCCESS -> {
                    Log.i("get user success","")
                    println("user main observerrrrrrrrrrrrr")
                    if(user.data == null){
                        mainFragmentBinding.progressBarFragmentMain.hide()
                        mainFragmentBinding.scrollViewFragmentMain.show()

                        val loginActivity = Intent(activity, LoginActivity::class.java)
                        startActivity(loginActivity)
                        activity?.finish()
                    }
                }
                Status.ERROR -> {
                    this.toast(user.message.toString())
                    mainFragmentBinding.progressBarFragmentMain.hide()

                    Log.i("user error",user.message.toString())
                }
                Status.LOADING -> {
                    mainFragmentBinding.progressBarFragmentMain.show()
                    Log.i("user loading","")
                }
            }
        }

        categoryViewModel.categoryLiveData.observe(viewLifecycleOwner){ list ->
            when(list.status){
                Status.SUCCESS -> {
                    mainFragmentBinding.progressBarFragmentMain.hide()
                    mainFragmentBinding.scrollViewFragmentMain.show()

                    Log.i("get category success","")

                    if(list.data != null) {
                        list?.let {
                            productViewModel.updateProductCategories(it.data!!)
                            categoryAdapter.updateCategoryList(it.data)
                        }
                    }
                }
                Status.ERROR -> {
                    mainFragmentBinding.textViewError.show()
                    mainFragmentBinding.textViewError.text = list.message.toString()
                    Log.i("category live data error",list.message.toString())
                }
                Status.LOADING -> {
                    mainFragmentBinding.progressBarFragmentMain.show()
                    mainFragmentBinding.scrollViewFragmentMain.hide()

                    Log.i("category live data loading","")
                }
            }

        }

        productViewModel.allProductLiveData.observe(viewLifecycleOwner){ products ->

            when(products.status){
                Status.SUCCESS -> {
                    Log.i("get all products success","")

                    if(products.data != null){
                        products?.let {
                            allProductAdapter.updateProductList(products.data)

                            val searchAdapter = SearchAdapter(requireContext(), products.data!!, object : ItemClickListener{
                                override fun onItemClick(Item: Any) {
                                    mainFragmentBinding.editTextSearch.text.clear()
                                    Item as ProductResponse
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
                Status.ERROR -> {
                    mainFragmentBinding.textViewError.show()
                    mainFragmentBinding.textViewError.text = products.message.toString()
                    Log.e("all products observe error",products.message.toString())
                }
                Status.LOADING -> {
                    Log.e("all products observe loading","")
                }
            }


        }

        productViewModel.newInProductLiveData.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS -> {
                    Log.i("get new in success","")

                    if(it.data != null){
                        newInProductAdapter.updateProductList(it.data)
                    }
                }
                Status.ERROR -> {
                    mainFragmentBinding.textViewError.show()
                    mainFragmentBinding.textViewError.text = it.message.toString()
                    Log.i("get new in product error",it.message.toString())
                }
                Status.LOADING -> {
                    Log.i("get new in product loading","")
                }
            }
        }

        productColorViewModel.productColorLiveData.observe(viewLifecycleOwner){ colors ->
            when(colors.status){
                Status.SUCCESS -> {
                    Log.i("get color success","")

                    if(colors.data != null){
                        productViewModel.updateProductColorName(colors.data)
                    }
                }
                Status.ERROR -> {
                    mainFragmentBinding.textViewError.show()
                    mainFragmentBinding.textViewError.text = colors.message.toString()
                    Log.i("get colors error",colors.message.toString())
                }
                Status.LOADING -> {
                    Log.i("get colors loading","")
                }
            }
        }

        productSizeViewModel.productSizeLiveData.observe(viewLifecycleOwner){ sizeList ->
            when(sizeList.status){
                Status.SUCCESS -> {
                    Log.i("get size success","")

                    if(sizeList.data != null) {
                        productViewModel.updateProductSizeName(sizeList.data)
                    }
                }
                Status.ERROR -> {
                    mainFragmentBinding.textViewError.show()
                    mainFragmentBinding.textViewError.text = sizeList.message.toString()
                    Log.i("get size error",sizeList.message.toString())
                }
                Status.LOADING -> {
                    Log.i("get size loading","")
                }
            }

        }

    }

}