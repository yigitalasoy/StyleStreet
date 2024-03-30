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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentMainBinding
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.util.Resource
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductColorViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductSizeViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _mainFragmentBinding: FragmentMainBinding? = null
    private val mainFragmentBinding get() = _mainFragmentBinding!!

    //val userViewModel: UserViewModel by viewModels()
    //val categoryViewModel: CategoryViewModel by viewModels()
    //val productSizeViewModel: ProductSizeViewModel by viewModels()
    //val productColorViewModel: ProductColorViewModel by viewModels()
    //val productViewModel: ProductViewModel by viewModels()

    @Inject lateinit var productViewModel: ProductViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var categoryViewModel: CategoryViewModel
    @Inject lateinit var productSizeViewModel: ProductSizeViewModel
    @Inject lateinit var productColorViewModel: ProductColorViewModel


    private val categoryAdapter = CategoryAdapter(arrayListOf())

    private val productAdapter = ProductAdapter(arrayListOf())


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

        productViewModel.getNewInProduct()


        val istanbulDateTime = LocalDateTime.now(ZoneId.of("Europe/Istanbul"))
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formattedDateTime = istanbulDateTime.format(formatter)

        println("güncel saat: $formattedDateTime")

        println("main fragment ürünü: ${ productViewModel.productLiveData.value?.data }")

        mainFragmentBinding.reyclerViewCategory.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter = categoryAdapter
        }

        mainFragmentBinding.recyclerViewTopSelling.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = productAdapter
        }

        categoryViewModel.getAllCategories()
        productColorViewModel.getAllProductColors()
        productSizeViewModel.getAllProductSize()

        //productViewModel.getNewInProduct()

        println("MAİN FRAGMENT ÜRÜN: ${productViewModel.productLiveData.value?.data}")





        mainFragmentBinding.buttonCikisYap.setOnClickListener {
            Firebase.auth.signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleServerClientId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(it.context,gso)
            googleSignInClient.signOut().addOnCompleteListener {
                Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
            }
            userViewModel.isLogin.value = Resource.success(false)
        }

        observer()

    }

    private fun observer() {
        userViewModel.isLogin.observe(viewLifecycleOwner){
            if(!it.data!!){
                //val loginActivity
                Log.e("FİREBASE GOOGLE LOGİN","SUCCESFULLY SIGNOUT")

                val loginActivity = Intent(activity, LoginActivity::class.java)
                startActivity(loginActivity)
                activity?.finish()
            }
        }

        categoryViewModel.categoryLiveData.observe(viewLifecycleOwner){ list ->
            list?.let {

                productViewModel.updateProductCategories(it.data!!)

                categoryAdapter.updateCategoryList(it.data)

            }
        }

        productViewModel.productLiveData.observe(viewLifecycleOwner){ products ->
            products?.let {
                if(it.data != null){
                    productAdapter.updateProductList(it.data)
                    for (a in products.data!!){
                        for (b in a.allProducts){
                            println("colorname: ${b.productColor.colorName}")
                        }
                    }
                }
            }
        }

        productColorViewModel.productColorLiveData.observe(viewLifecycleOwner) { colors ->
            colors?.let {

                productViewModel.updateProductColorName(it.data!!)

                for (color in it.data){
                    println("color: ${color.colorId} \t ${color.colorName}")
                }

            }
        }

        productSizeViewModel.productSizeLiveData.observe(viewLifecycleOwner) { sizeList ->
            sizeList?.let {
                //productColorandSize.value = arrayListOf(productColorandSize.value?.get(1)!!,true)

                productViewModel.updateProductSizeName(it.data!!)

                for (size in it.data){
                    println("size: ${size.productSizeId} \t ${size.sizeName}")
                }
            }
        }
    }
}