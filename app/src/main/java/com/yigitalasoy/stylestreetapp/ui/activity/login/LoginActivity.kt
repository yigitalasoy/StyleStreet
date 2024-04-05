package com.yigitalasoy.stylestreetapp.ui.activity.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yigitalasoy.stylestreetapp.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var loginActivityBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)
    }
}