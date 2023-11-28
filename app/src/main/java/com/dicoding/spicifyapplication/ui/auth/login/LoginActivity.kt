package com.dicoding.spicifyapplication.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.MainActivity
import com.dicoding.spicifyapplication.databinding.ActivityLoginBinding
import com.dicoding.spicifyapplication.ui.auth.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnTvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnSigIn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}