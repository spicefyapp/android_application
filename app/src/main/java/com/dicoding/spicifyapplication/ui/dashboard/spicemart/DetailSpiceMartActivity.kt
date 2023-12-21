package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.databinding.ActivityDetailSpiceMartBinding
import java.io.IOException
import java.util.Locale

class DetailSpiceMartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailSpiceMartBinding
    private lateinit var spiceProduk: ProductSpiceModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSpiceMartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val geocoder = Geocoder(this, Locale.getDefault())

        fun getAddressDetails(lat: Double, lon: Double): String {
            try {
                val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)

                if (addresses?.isNotEmpty() == true) {
                    val address: Address = addresses[0]

                    val thoroughfare = address.thoroughfare ?: ""
                    val subLocality = address.subLocality ?: "Unknown SubLocality"
                    val locality = address.locality ?: "Unknown Locality"

                    return "$thoroughfare $subLocality, $locality"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return "Alamat tidak ditemukan"
        }

        val bundle = intent.extras
        if (bundle != null) {
            spiceProduk = ProductSpiceModel(
                dataNama = bundle.getString("Title"),
                dataDes = bundle.getString("Description"),
                dataHarga = bundle.getString("Price"),
                dataWa = bundle.getString("Wa"),
                dataImage = bundle.getString("Image"),
                dataLat = bundle.getDouble("Lat"),
                dataLon = bundle.getDouble("Lon"),
            )

            // Menampilkan detail produk
            binding.titleTxt.text = spiceProduk.dataNama
            binding.descriptionTxt.text = spiceProduk.dataDes
            binding.priceTxt.text = spiceProduk.dataHarga
            binding.tvWa.text = spiceProduk.dataWa
            Glide.with(this).load(spiceProduk.dataImage).into(binding.itemPic)

            // Menampilkan alamat
            val alamatJalan = getAddressDetails(spiceProduk.dataLat!!, spiceProduk.dataLon!!)
            binding.lokasiTxt.text = alamatJalan

            binding.btnOrder.setOnClickListener {
                val url = "https://wa.me/${spiceProduk.dataWa}?text=Hi, Is any one Available?"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                startActivity(intent)
            }
        }
    }
}