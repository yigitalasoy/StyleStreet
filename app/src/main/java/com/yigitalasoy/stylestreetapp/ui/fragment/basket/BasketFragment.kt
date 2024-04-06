package com.yigitalasoy.stylestreetapp.ui.fragment.basket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.FragmentBasketBinding
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BasketFragment : Fragment() {

    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productViewModel: ProductViewModel

    var basketProductAdapter = BasketProductAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasketBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basketViewModel.getBasketData(userViewModel.userLiveData.value!!.data!!.id!!)

        binding.recyclerViewBasketProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = basketProductAdapter
        }

        observer()
    }

    fun observer(){
        basketViewModel.basketLiveData.observe(viewLifecycleOwner){
            if(it.data != null){
                Log.e("basket: ","basket datası başarıyla geldi.")
                basketViewModel.getBasketProducts(productViewModel.allProductLiveData.value!!.data!!)
            }
        }


        basketViewModel.basketSubProductsLiveData.observe(viewLifecycleOwner){
            if(it.data != null){
                Log.e("basket ürünleri: ","basket ürünleri başarıyla geldi.")
                basketProductAdapter.updateBasketSubProductList(it.data)
            }
        }
    }

}