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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.data.network.response.SpiceItem
import com.dicoding.spicifyapplication.databinding.ActivityMapsBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.helper.ViewModelFactory
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.DetailSpiceMartActivity
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.SpiceMartViewModel
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

    private val viewModel by viewModels<SpiceMartViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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

        proccessGetStoriesWithLocation()
        getMyLocation()

    }

    private fun proccessGetStoriesWithLocation() {
        viewModel.getStoriesWithLocation().observe(this){ result ->
            if (result != null){
                when (result) {
                    is ResultState.Loading -> {}
                    is ResultState.Success -> {
                        result.data?.forEach { data ->
                            val latLng = LatLng(data?.lat!!, data?.lan!!)
                            val marker = mMap.addMarker(MarkerOptions().position(latLng).title(data.name))
                            marker?.tag = data
                            boundsBuilder.include(latLng)
                        }
                        mMap.setOnMarkerClickListener { marker ->
                            val productSpiceModel: SpiceItem? = marker.tag as? SpiceItem
                            productSpiceModel?.let { showBottomSheet(it) }
                            true
                        }
                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )
                    }
                    is ResultState.Error -> {}
                }
            }

        }

    }

    private fun showBottomSheet(productSpiceModel: SpiceItem) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_detailmaps, null)

        val productImageView = bottomSheetView.findViewById<ImageView>(R.id.ivProductSpice)
        val productNameTextView = bottomSheetView.findViewById<TextView>(R.id.tvNameProduct)
        val productAddressTextView = bottomSheetView.findViewById<TextView>(R.id.tvAddressProduct)
        val address = getAddressFromLocation(productSpiceModel.lat, productSpiceModel.lan)
        val detailButton = bottomSheetView.findViewById<ImageView>(R.id.btnDetailProduct)


        Glide.with(this).load(productSpiceModel.image).into(productImageView)
        productNameTextView.text = productSpiceModel.name
        productAddressTextView.text = address

        detailButton.setOnClickListener {
            val intent = Intent(this, DetailSpiceMartActivity::class.java)
            intent.putExtra("Image", productSpiceModel.image)
            intent.putExtra("Description", productSpiceModel.description)
            intent.putExtra("Title", productSpiceModel.name)
            intent.putExtra("Price", productSpiceModel.price)
            intent.putExtra("Wa", productSpiceModel.noWA)
            intent.putExtra("Lat", productSpiceModel.lat)
            intent.putExtra("Lon", productSpiceModel.lan)
            startActivity(intent)
            bottomSheetDialog.dismiss()
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
    private val boundsBuilder = LatLngBounds.Builder()

    companion object {
        private const val TAG = "MapsActivity"
    }
}