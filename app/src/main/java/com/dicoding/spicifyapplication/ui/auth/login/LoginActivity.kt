package com.dicoding.spicifyapplication.ui.auth.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.databinding.ActivityLoginBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.auth.register.RegisterActivity
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnTvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnSigIn.setOnClickListener {
            processLogin()
        }
        setupView()
    }

    private fun processLogin() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()
            viewModel.login( email, password).observe(this@LoginActivity) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                            btnSigIn.isEnabled = false
                        }
                        is ResultState.Success -> {
                            showLoading(false)
                            btnSigIn.isEnabled = true
                            viewModel.setlogin(UserModel(email,result.data.accessToken!!))
                            showToast(result.data.status.toString())
//                            moveToMainActivity()

                        }
                        is ResultState.Error -> {
                            showLoading(false)
                            btnSigIn.isEnabled = true
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}