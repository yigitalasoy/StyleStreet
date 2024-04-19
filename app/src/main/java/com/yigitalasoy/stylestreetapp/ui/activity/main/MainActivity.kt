package com.yigitalasoy.stylestreetapp.ui.activity.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.yigitalasoy.stylestreetapp.R
import com.yigitalasoy.stylestreetapp.databinding.ActivityMainBinding
import com.yigitalasoy.stylestreetapp.ui.activity.basket.BasketActivity
import com.yigitalasoy.stylestreetapp.ui.activity.login.LoginActivity
import com.yigitalasoy.stylestreetapp.ui.fragment.main.MainFragment
import com.yigitalasoy.stylestreetapp.ui.fragment.notification.NotificationFragment
import com.yigitalasoy.stylestreetapp.ui.fragment.profile.ProfileFragment
import com.yigitalasoy.stylestreetapp.ui.fragment.wishlist.WishListFragment
import com.yigitalasoy.stylestreetapp.util.Status
import com.yigitalasoy.stylestreetapp.util.hide
import com.yigitalasoy.stylestreetapp.util.show
import com.yigitalasoy.stylestreetapp.util.toast
import com.yigitalasoy.stylestreetapp.viewmodel.BasketViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.CategoryViewModel
import com.yigitalasoy.stylestreetapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding: ActivityMainBinding

    @Inject lateinit var basketViewModel: BasketViewModel
    @Inject lateinit var categoryViewModel: CategoryViewModel
    @Inject lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        mainActivityBinding.apply {
            constraintHome.setOnClickListener {
                homeButton()
            }

            constraintWishList.setOnClickListener {
                if(categoryViewModel.categoryLiveData.value?.status != Status.LOADING) {
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

                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainerView, WishListFragment())
                    fragmentManager.popBackStack()
                    fragmentTransaction.commit()
                }
            }

            constraintProfile.setOnClickListener {

                if(categoryViewModel.categoryLiveData.value?.status != Status.LOADING) {
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

                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainerView, ProfileFragment())
                    fragmentManager.popBackStack()
                    fragmentTransaction.commit()
                }
            }

            constraintNotification.setOnClickListener {

                if(categoryViewModel.categoryLiveData.value?.status != Status.LOADING) {
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

                    val fragmentManager = supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainerView, NotificationFragment())
                    fragmentManager.popBackStack()
                    fragmentTransaction.commit()

                }

            }

            floatingButtonBasket.setOnClickListener {
                if(categoryViewModel.categoryLiveData.value?.status != Status.LOADING) {
                    val basketActivity = Intent(this@MainActivity, BasketActivity::class.java)
                    startActivity(basketActivity)
                }
            }
            observe()
        }

        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.create()
                alertDialog.setTitle("Log out")
                alertDialog.setMessage("Are you sure for log out ?")
                alertDialog.setPositiveButton("Yes",DialogInterface.OnClickListener { dialog, which ->
                    this@MainActivity.toast("YES BASILDI")

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    this@MainActivity.startActivity(intent)
                    userViewModel.signOut(this@MainActivity)
                    this@MainActivity.finish()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    this@MainActivity.toast("NO BASILDI")

                })
                alertDialog.show()
            }
        })
    }

    fun homeButton() {
        if(categoryViewModel.categoryLiveData.value?.status != Status.LOADING) {
            mainActivityBinding.apply {

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

                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainerView, MainFragment())
                fragmentManager.popBackStack()
                fragmentTransaction.commit()

            }
        }
    }

    private fun observe(){

        basketViewModel.basketLiveData.observe(this){
            when(it.status){
                Status.SUCCESS -> {
                    Log.i("basket live data success","")

                    if(it.data != null){
                        mainActivityBinding.textViewBasketQuantity.text = basketViewModel.basketLiveData.value?.data?.basketProducts?.size.toString()
                    }
                }
                Status.ERROR -> {
                    Log.i("basket live data error",it.message.toString())

                }
                Status.LOADING -> {
                    Log.i("basket live data loading","")
                }
            }
        }
    }
}