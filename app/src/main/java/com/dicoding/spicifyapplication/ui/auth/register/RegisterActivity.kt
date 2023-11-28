package com.dicoding.spicifyapplication.ui.auth.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivityRegisterBinding
import com.dicoding.spicifyapplication.ui.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTvSigin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}