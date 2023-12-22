package com.dicoding.spicifyapplication.ui.adapter

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.databinding.ItemSpicemartRowBinding
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.DetailSpiceMartActivity
import java.io.IOException
import java.util.Locale

class AdapterSpicesMart (private var dataProduct: List<ProductSpiceModel>) : RecyclerView.Adapter<AdapterSpicesMart.MyViewHolder>() {
    class MyViewHolder(binding: ItemSpicemartRowBinding) : RecyclerView.ViewHolder(binding.root) {

        var ivImage = binding.ivProduct
        var tvName = binding.tvNameProduct
        var tvPrice = binding.tvPriceProduct
        var tvLoc = binding.tvLocProduct

        fun bind(data : ProductSpiceModel) {
            val geocoder = Geocoder(itemView.context, Locale.getDefault())

            fun getAddressDetails(lat: Double, lon: Double): String {
                try {
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)

                    if (addresses?.isNotEmpty() == true) {
                        val address: Address = addresses[0]

                        val thoroughfare = address.thoroughfare ?: ""
                        val subLocality = address.subLocality ?: ""
                        val locality = address.locality ?: ""

                        return "$thoroughfare $subLocality, $locality"
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return "Alamat tidak ditemukan"
            }

            val alamatJalan = getAddressDetails(data.dataLat!!, data.dataLon!!)


            Glide.with(itemView).load(data.dataImage)
                .into(ivImage)
            tvName.text = data.dataNama
            tvPrice.text = data.dataHarga
            tvLoc.text = alamatJalan


            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailSpiceMartActivity::class.java)
                intent.putExtra("Image", data.dataImage)
                intent.putExtra("Description", data.dataDes)
                intent.putExtra("Title", data.dataNama)
                intent.putExtra("Price", data.dataHarga)
                intent.putExtra("Wa", data.dataWa)
                intent.putExtra("Lat", data.dataLat)
                intent.putExtra("Lon", data.dataLon)
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSpicemartRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = dataProduct.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])    }
}