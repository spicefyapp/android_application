package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.data.network.response.SpiceItem
import com.dicoding.spicifyapplication.databinding.ActivitySpiceMartBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.helper.ViewModelFactory
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpiceMart
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpicesMart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SpiceMartActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpiceMartBinding

    private val viewModel by viewModels<SpiceMartViewModel> {
        ViewModelFactory.getInstance(this)
    }

    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    private lateinit var dataList: ArrayList<ProductSpiceModel>
    private lateinit var adapter: AdapterSpicesMart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpiceMartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val gridLayoutManager = GridLayoutManager(this@SpiceMartActivity, 2)
        binding.rvSpiceMart.layoutManager = gridLayoutManager

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this,AddProductActivity::class.java))
        }

        processGetAllSpice()
    }

    private fun processGetData() {
        showLoading(true)
        dataList = ArrayList()
        adapter = AdapterSpicesMart(dataList)
        binding.rvSpiceMart.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("produk")
        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(ProductSpiceModel::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                showLoading(false)

            }
            override fun onCancelled(error: DatabaseError) {
                showLoading(false)
            }
        })
    }

    private fun processGetAllSpice() {
        viewModel.getProductSpice().observe(this) { result ->
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setListStory(listSpice: List<SpiceItem?>) {
        val adapter = AdapterSpiceMart()
        adapter.submitList(listSpice)
        binding.rvSpiceMart.adapter = adapter
    }
}