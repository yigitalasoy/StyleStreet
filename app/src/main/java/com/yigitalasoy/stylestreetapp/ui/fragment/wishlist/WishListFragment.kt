package com.yigitalasoy.stylestreetapp.ui.fragment.wishlist

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.FragmentWishListBinding
import com.yigitalasoy.stylestreetapp.model.ProductResponse
import com.yigitalasoy.stylestreetapp.ui.fragment.main.MainFragment
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.ProductViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import javax.inject.Inject


@AndroidEntryPoint
class WishListFragment : Fragment() {

    @Inject lateinit var wishListViewModel: WishListViewModel
    @Inject lateinit var userViewModel: UserViewModel
    @Inject lateinit var productViewModel: ProductViewModel

    var _binding: FragmentWishListBinding? = null
    val binding get() = _binding!!

    var wishListAdapter = WishListAdapter(arrayListOf(), arrayListOf())
    val wishListProducts: ArrayList<ProductResponse> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("oncreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("onCreateView")
        _binding = FragmentWishListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")
        wishListViewModel.getWishList(userViewModel.userLiveData.value?.data?.id!!)


        //wishListViewModel.getWishList(userViewModel.userLiveData.value?.data?.id!!)



        /*wishListViewModel.wishListLiveData.value?.data!!.forEach {wishListResponse ->
            wishListProducts.add(productViewModel.allProductLiveData.value?.data?.find { it.productId == wishListResponse.Product_Id }!!)
        }*/

        wishListViewModel.wishListLiveData.value?.data?.let {

            println("data let: $it")
            wishListAdapter = WishListAdapter(it,wishListProducts)

            it.forEach {wishListResponse ->
                println("every response: $wishListResponse")
                wishListProducts.add(productViewModel.allProductLiveData.value?.data?.find { it.productId == wishListResponse.product_Id }!!)
            }

            println("wishListProducts eklendi: ${wishListProducts}")
            println("it: ${it}")

            wishListAdapter.updateWishList(it)

        }



        binding.apply {

            recyclerViewWishList.apply {
                adapter = wishListAdapter
                layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            }

            buttonStartShopping.setOnClickListener {


                val fragmentManager = activity?.supportFragmentManager
                val fragmentTransaction = fragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.fragmentContainerView, MainFragment())
                fragmentManager?.popBackStack()
                fragmentTransaction?.commit()


            }

            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    wishListViewModel.removeProductToWishList(wishListViewModel.wishListLiveData.value?.data?.get(viewHolder.bindingAdapterPosition)?.wish_Id!!)
                    //wishListAdapter.deleteItem()
                }


                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )


                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(R.color.black)
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.remove_red
                            )
                        )
                        .addActionIcon(R.drawable.dustbin)
                        .create()
                        .decorate()

                }
            })

            itemTouchHelper.attachToRecyclerView(binding.recyclerViewWishList)


            editTextSearch.doOnTextChanged { text, start, before, count ->
                wishListAdapter.filterWishList(text.toString())
            }

        }

        observe()
    }


    fun observe(){
        wishListViewModel.wishListLiveData.observe(viewLifecycleOwner){wishList->
            wishList?.let {
                if(it.data?.size != 0){

                    /*wishListAdapter = WishListAdapter(wishList.data!!,wishListProducts)

                    wishList.data.forEach {wishListResponse ->
                        println("every response: $wishListResponse")
                        wishListProducts.add(productViewModel.allProductLiveData.value?.data?.find { it.productId == wishListResponse.Product_Id }!!)
                    }

                    println("wishListProducts eklendi: ${wishListProducts}")
                    println("it: ${it}")

                    wishListAdapter.updateWishList(wishList.data)*/


                    println("observe wish list data geldi: $wishList")
                    wishListAdapter.updateWishList(wishList.data!!)
                    binding.recyclerViewWishList.show()
                    binding.constraintLayoutEmptyList.hide()
                } else {
                    binding.recyclerViewWishList.hide()
                    binding.constraintLayoutEmptyList.show()
                }
            }
        }

        /*wishListViewModel.wishListError.observe(this.viewLifecycleOwner){
            it?.let{
                if(it.message != null){
                    println("WİSH LİST VİEW MODEL ERROR: ${it.message}")
                }
            }
        }*/

    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("destroy edildi.")
        wishListViewModel.wishListError.removeObservers(viewLifecycleOwner)
    }

}