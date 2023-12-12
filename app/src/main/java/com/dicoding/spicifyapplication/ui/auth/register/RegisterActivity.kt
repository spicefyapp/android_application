package com.dicoding.spicifyapplication.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.databinding.ActivityRegisterBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.auth.login.LoginActivity
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTvSigin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnSignUp.setOnClickListener { processRegister() }
    }

    private fun processRegister() {

        binding.apply {
            val name = edRegisterName.text.toString()
            val email = edRegisterEmail.text.toString()
            val password = edRegisterPassword.text.toString()

            viewModel.register(name, email, password).observe(this@RegisterActivity) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                            btnSignUp.isEnabled = false
                        }
                        is ResultState.Success -> {
                            showLoading(false)
                            btnSignUp.isEnabled = true
                            showToast(result.data.message!!)
                        }
                        is ResultState.Error -> {
                            showLoading(false)
                            btnSignUp.isEnabled = true
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
}