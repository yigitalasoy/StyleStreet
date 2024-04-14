package com.yigitalasoy.stylestreetapp.ui.fragment.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.databinding.FragmentWishListBinding
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WishListFragment : Fragment() {

    @Inject lateinit var wishListViewModel: WishListViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productViewModel: ProductViewModel

    var _binding: FragmentWishListBinding? = null
    val binding get() = _binding!!

    lateinit var wishListAdapter: WishListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWishListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wishListViewModel.getWishList(userViewModel.userLiveData.value?.data?.id!!)

        wishListAdapter = WishListAdapter(wishListViewModel.wishListLiveData.value?.data!!,productViewModel.allProductLiveData.value?.data!!)
        wishListAdapter.updateWishList(wishListViewModel.wishListLiveData.value?.data!!)

        binding.apply {

            recyclerViewWishList.apply {
                adapter = wishListAdapter
                layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            }

        }

        observe()
    }


    fun observe(){
        wishListViewModel.wishListLiveData.observe(viewLifecycleOwner){wishList->
            wishList?.let {
                if(it.data?.size != 0){
                    println("observe wish list data geldi: $wishList")
                    wishListAdapter.updateWishList(wishList.data!!)
                }
            }
        }

    }


}