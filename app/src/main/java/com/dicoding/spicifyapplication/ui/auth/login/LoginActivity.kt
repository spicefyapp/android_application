package com.dicoding.spicifyapplication.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.spicifyapplication.MainActivity
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.data.model.UserModel
import com.dicoding.spicifyapplication.databinding.ActivityLoginBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.auth.register.RegisterActivity
import com.dicoding.spicifyapplication.helper.ViewModelFactory

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

        setupListener()


    }

    private fun setupListener(){
        binding.btnTvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            processLogin()
        }
    }

    private fun processLogin() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()

            if (email.isBlank()) {
                showToast(getString(R.string.email_must_be_filled_in))
                edLoginEmail.requestFocus()
                return
            }

            if (password.isBlank()) {
                showToast(getString(R.string.password_must_be_filled_in))
                edLoginPassword.requestFocus()
                return
            }

            viewModel.login( email, password).observe(this@LoginActivity) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                            btnSignIn.isEnabled = false
                        }
                        is ResultState.Success -> {
                            showLoading(false)
                            btnSignIn.isEnabled = true
                            viewModel.setlogin(UserModel(email,result.data.accessToken!!))
                            showToast(getString(R.string.login_successfully))
                            moveToMainActivity()

                        }
                        is ResultState.Error -> {
                            showLoading(false)
                            btnSignIn.isEnabled = true
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }
    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
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