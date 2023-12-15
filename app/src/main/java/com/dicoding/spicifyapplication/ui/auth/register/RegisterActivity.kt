package com.dicoding.spicifyapplication.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.R
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

            if (name.isBlank()) {
                showToast(getString(R.string.name_must_be_filled_in))
                edRegisterName.requestFocus()
                return
            }

            if (email.isBlank()) {
                showToast(getString(R.string.email_must_be_filled_in))
                edRegisterEmail.requestFocus()
                return
            }

            if (password.isBlank()) {
                showToast(getString(R.string.password_must_be_filled_in))
                edRegisterPassword.requestFocus()
                return
            }

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
                            moveLoginActivity()
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

    private fun moveLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}