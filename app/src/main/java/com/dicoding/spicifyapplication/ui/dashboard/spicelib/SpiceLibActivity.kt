package com.dicoding.spicifyapplication.ui.dashboard.spicelib

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.databinding.ActivitySpiceLibBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpices
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class SpiceLibActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpiceLibBinding
    private val viewModel by viewModels<SpiceLibViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpiceLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvSpiceLib.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvSpiceLib.addItemDecoration(itemDecoration)

        supportActionBar?.hide()
        binding.btnBack.setOnClickListener { onBackPressed() }
        processGetAllSpice()


    }

    private fun processGetAllSpice() {
        viewModel.getSpices().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)

                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        setListStory(result.data!!)
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        showToast("Tidak ada data")


                    }

                    else -> {}
                }
            }
        }
    }

    private fun setListStory(listSpice: List<RempahItem?>) {
        val adapter = AdapterSpices()
        adapter.submitList(listSpice)
        binding.rvSpiceLib.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}