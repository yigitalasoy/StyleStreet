package com.yigitalasoy.stylestreetapp.ui.Login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yigitalasoy.stylestreetapp.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    //private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        //val navHostFragment = supportFragmentManager
        //    .findFragmentById(R.id.fragment) as NavHostFragment

        //navController = navHostFragment.navController

        //supportActionBar?.apply {
        //    setDisplayHomeAsUpEnabled(true)
        //    setHomeAsUpIndicator(R.drawable.back_arrow)
        //}
        //setupActionBarWithNavController(navController)

    }

    fun login(view: View){
        //Toast.makeText(applicationContext,loginBinding.editTextEmail.text.toString()+loginBinding.editTextPassword.text.toString(),Toast.LENGTH_LONG).show()
    }

    //override fun onSupportNavigateUp(): Boolean {
    //    return navController.navigateUp() || super.onSupportNavigateUp()
    //}



}