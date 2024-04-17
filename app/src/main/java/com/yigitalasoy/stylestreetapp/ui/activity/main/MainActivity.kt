package com.yigitalasoy.stylestreetapp.ui.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.NavHostFragment
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ActivityMainBinding
import com.yigitalasoy.stylestreetapp.ui.activity.basket.BasketActivity
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding: ActivityMainBinding

    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)



        val navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

        mainActivityBinding.apply {
                constraintHome.setOnClickListener {

                    if(!(categoryViewModel.categoryLoading.value?.data!!)) {
                        imageViewHome.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.home_filled,
                                null
                            )
                        )
                        imageViewWishList.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.heart,
                                null
                            )
                        )
                        imageViewProfile.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.profile,
                                null
                            )
                        )
                        imageViewNotification.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.notification,
                                null
                            )
                        )

                        imageViewStickHome.show()
                        imageViewStickHearth.hide()
                        imageViewStickProfile.hide()
                        imageViewStickNotification.hide()


                        navController.navigate(R.id.mainFragment)
                    }

                }

                constraintWishList.setOnClickListener {
                    if(!(categoryViewModel.categoryLoading.value?.data!!)) {
                        imageViewHome.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.home,
                                null
                            )
                        )
                        imageViewWishList.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.heart_filled,
                                null
                            )
                        )
                        imageViewProfile.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.profile,
                                null
                            )
                        )
                        imageViewNotification.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.notification,
                                null
                            )
                        )

                        imageViewStickHome.hide()
                        imageViewStickHearth.show()
                        imageViewStickProfile.hide()
                        imageViewStickNotification.hide()
                        navController.navigate(R.id.wishListFragment)
                    }

                }

                constraintProfile.setOnClickListener {

                    if(!(categoryViewModel.categoryLoading.value?.data!!)) {
                        imageViewHome.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.home,
                                null
                            )
                        )
                        imageViewWishList.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.heart,
                                null
                            )
                        )
                        imageViewProfile.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.profile_filled,
                                null
                            )
                        )
                        imageViewNotification.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.notification,
                                null
                            )
                        )

                        imageViewStickHome.hide()
                        imageViewStickHearth.hide()
                        imageViewStickProfile.show()
                        imageViewStickNotification.hide()
                        navController.navigate(R.id.profileFragment)
                    }

                }

                constraintNotification.setOnClickListener {

                    if(!(categoryViewModel.categoryLoading.value?.data!!)) {
                        imageViewHome.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.home,
                                null
                            )
                        )
                        imageViewWishList.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.heart,
                                null
                            )
                        )
                        imageViewProfile.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.profile,
                                null
                            )
                        )
                        imageViewNotification.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.notification_filled,
                                null
                            )
                        )

                        imageViewStickHome.hide()
                        imageViewStickHearth.hide()
                        imageViewStickProfile.hide()
                        imageViewStickNotification.show()
                        navController.navigate(R.id.notificationFragment)
                    }

                }

                floatingButtonBasket.setOnClickListener {

                    /*imageViewHome.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.home,null))
                    imageViewWishList.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.heart,null))
                    imageViewProfile.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.profile,null))
                    imageViewNotification.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.notification,null))

                    imageViewStickHome.hide()
                    imageViewStickHearth.hide()
                    imageViewStickProfile.hide()
                    imageViewStickNotification.hide()*/

                    if(!(categoryViewModel.categoryLoading.value?.data!!)) {
                        val basketActivity = Intent(this@MainActivity, BasketActivity::class.java)
                        startActivity(basketActivity)
                    }

                    //navController.navigate(R.id.basketFragment)

                }

                observe()

            }





    }

    fun observe(){

        basketViewModel.basketLiveData.observe(this){
            if(it.data != null){
                mainActivityBinding.textViewBasketQuantity.text = basketViewModel.basketLiveData.value?.data?.basketProducts?.size.toString()
            }
        }

    }



}