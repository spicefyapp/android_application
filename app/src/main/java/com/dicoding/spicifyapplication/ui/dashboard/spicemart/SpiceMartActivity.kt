package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.databinding.ActivitySpiceMartBinding
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpicesMart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SpiceMartActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpiceMartBinding

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

        processGetData()

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
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}