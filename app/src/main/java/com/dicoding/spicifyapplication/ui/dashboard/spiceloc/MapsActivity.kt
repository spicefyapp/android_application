package com.dicoding.spicifyapplication.ui.dashboard.spiceloc

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.databinding.ActivityMapsBinding
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.DetailSpiceMartActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("produk")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fetchDataAndAddMarkers()
        getMyLocation()

    }

    private fun showBottomSheet(productSpiceModel: ProductSpiceModel) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_detailmaps, null)

        val productImageView = bottomSheetView.findViewById<ImageView>(R.id.productImageView)
        val productNameTextView = bottomSheetView.findViewById<TextView>(R.id.productNameTextView)
        val productDescriptionTextView = bottomSheetView.findViewById<TextView>(R.id.productDescriptionTextView)
        val productAddressTextView = bottomSheetView.findViewById<TextView>(R.id.productAddressTextView)
        val address = getAddressFromLocation(productSpiceModel.dataLat, productSpiceModel.dataLon)
        val detailButton = bottomSheetView.findViewById<Button>(R.id.btnDetailMaps) // Tambahkan ini


        Glide.with(this).load(productSpiceModel.dataImage).into(productImageView)
        productNameTextView.text = productSpiceModel.dataNama
        productDescriptionTextView.text = productSpiceModel.dataHarga
        productAddressTextView.text = address

        // Menambahkan onClickListener untuk tombol detail
        detailButton.setOnClickListener {
            // Handling untuk berpindah ke halaman detail product
            val intent = Intent(this, DetailSpiceMartActivity::class.java)
            intent.putExtra("Image", productSpiceModel.dataImage)
            intent.putExtra("Description", productSpiceModel.dataDes)
            intent.putExtra("Title", productSpiceModel.dataNama)
            intent.putExtra("Price", productSpiceModel.dataHarga)
            intent.putExtra("Wa", productSpiceModel.dataWa)
            intent.putExtra("Lat", productSpiceModel.dataLat)
            intent.putExtra("Lon", productSpiceModel.dataLon)
            startActivity(intent)
            bottomSheetDialog.dismiss() // Menutup bottom sheet setelah berpindah halaman
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun getAddressFromLocation(lat: Double?, lon: Double?): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocation(lat ?: 0.0, lon ?: 0.0, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressStringBuilder = StringBuilder()

                // Concatenate address components
                for (i in 0..address.maxAddressLineIndex) {
                    addressStringBuilder.append(address.getAddressLine(i)).append(" ")
                }

                return addressStringBuilder.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "Alamat tidak ditemukan"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
//            val success =
//                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.")
//            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun fetchDataAndAddMarkers() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val spiceProduk = dataSnapshot.getValue(ProductSpiceModel::class.java)
                    spiceProduk?.let {
                        val latLng = LatLng(it.dataLat ?: 0.0, it.dataLon ?: 0.0)
                        val marker = mMap.addMarker(MarkerOptions().position(latLng).title(it.dataNama))
                        marker?.tag = it
                        boundsBuilder.include(latLng)
                    }
                }
                mMap.setOnMarkerClickListener { marker ->
                    val productSpiceModel: ProductSpiceModel? = marker.tag as? ProductSpiceModel

                    productSpiceModel?.let { showBottomSheet(it) }

                    true
                }

                val bounds: LatLngBounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        300)
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error jika diperlukan
            }
        })
    }
    private val boundsBuilder = LatLngBounds.Builder()

    companion object {
        private const val TAG = "MapsActivity"
    }
}