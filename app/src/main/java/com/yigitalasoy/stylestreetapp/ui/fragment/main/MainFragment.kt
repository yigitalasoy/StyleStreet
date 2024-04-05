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
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.ui.activity.productdetail.ProductDetailActivity
import com.yigitalasoy.stylestreetapp.util.ItemClickListener
import com.yigitalasoy.stylestreetapp.util.toast
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
    private lateinit var productAdapter: ProductAdapter


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


        productAdapter = ProductAdapter(arrayListOf(),object: ItemClickListener{
            override fun onItemClick(position: Int) {

                this@MainFragment.toast(productViewModel.productLiveData.value!!.data?.get(position)!!.productName)
                val productDetailActivity = Intent(activity, ProductDetailActivity::class.java)
                productDetailActivity.putExtra("selectedProductId",productViewModel.productLiveData.value!!.data?.get(position)!!.productId)
                productDetailActivity.putExtra("selectedSubProductId",productViewModel.productLiveData.value!!.data?.get(position)!!.allProducts[0].subProductId)
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

        mainFragmentBinding.recyclerViewTopSelling.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
            adapter = productAdapter
        }

        categoryViewModel.getAllCategories()
        productColorViewModel.getAllProductColors()
        productSizeViewModel.getAllProductSize()

        mainFragmentBinding.buttonCikisYap.setOnClickListener {

            //Firebase.auth.signOut()

            userViewModel.signOut(requireContext())

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleServerClientId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(it.context,gso)
            googleSignInClient.signOut().addOnCompleteListener {
                Log.e("GOOGLE SIGN OUT","SUCCUSFULLY SIGN OUT")
            }



            /*
            val loginActivity = Intent(activity, LoginActivity::class.java)
            startActivity(loginActivity)
            activity?.finish()

             */
        }

        observer()

    }

    private fun observer() {
        /*userViewModel.isLogin.observe(viewLifecycleOwner){
            println("login geldi. deger: ${it.data}")
            if(!it.data!!){
                //val loginActivity
                Log.e("FİREBASE GOOGLE LOGİN","SUCCESFULLY SIGNOUT")

                val loginActivity = Intent(activity, LoginActivity::class.java)
                startActivity(loginActivity)
                activity?.finish()


            }
        }*/

        userViewModel.userLiveData.observe(viewLifecycleOwner){ user ->
            if(user.data == null){
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
                }
            }
        }

        productColorViewModel.productColorLiveData.observe(viewLifecycleOwner) { colors ->
            colors?.let {
                productViewModel.updateProductColorName(it.data!!)

            }
        }

        productSizeViewModel.productSizeLiveData.observe(viewLifecycleOwner) { sizeList ->
            sizeList?.let {
                productViewModel.updateProductSizeName(it.data!!)
            }
        }
    }
}