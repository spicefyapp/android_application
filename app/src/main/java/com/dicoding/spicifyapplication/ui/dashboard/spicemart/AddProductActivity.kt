package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.databinding.ActivityAddProductBinding
import com.dicoding.spicifyapplication.helper.getImageUri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private var currentImageUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.ivAddImage.setOnClickListener {
            showImageDialog()
        }

        binding.btnSave.setOnClickListener {
            getMyLastLocation()
        }

    }

    private fun startCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(this,
                        getString(R.string.camera_permission_not_granted), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAddImage.setImageURI(it)
        }
    }
    private fun showImageDialog() {
        val options = arrayOf("Kamera", "Galeri")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Sumber Foto")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startCamera()
                    1 -> startGallery()
                }
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveProductToFirebase(location: Location) {
        val name = binding.edAddName.text.toString()
        val price = binding.edAddPrice.text.toString()
        val wa = binding.edAddWa.text.toString()
        val description = binding.edAddDesc.text.toString()

        if (name.isEmpty() || wa.isEmpty() || price.isEmpty() || description.isEmpty() || currentImageUri == null) {
            Toast.makeText(this, getString(R.string.please_fill_in_all_data), Toast.LENGTH_SHORT).show()
            return
        }

        if (!wa.startsWith("+62")) {
            Toast.makeText(this,
                getString(R.string.whatsapp_numbers_must_start_with_62), Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val imageName = "${System.currentTimeMillis()}.jpg"

        val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        currentImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val productModel = ProductSpiceModel(
                            dataNama = name,
                            dataHarga = price,
                            dataWa = wa,
                            dataDes = description,
                            dataImage = uri.toString(),
                            dataLat = location.latitude,
                            dataLon = location.longitude
                        )

                        val database = FirebaseDatabase.getInstance().reference
                        val produkRef = database.child("produk")

                        val produkKey = produkRef.push().key

                        produkKey?.let {
                            produkRef.child(it).setValue(productModel)
                        }

                        Toast.makeText(this,getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show()
                        showLoading(false)
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)

                }
        }
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    if (location != null){
                        if (binding.switchLoc.isChecked){
                            saveProductToFirebase(it)
                        }else{
                        }
                    }

                } ?: run {
                    Toast.makeText(
                        this@AddProductActivity,
                        getString(R.string.location_not_available_please_activate_your_gps),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@AddProductActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 123
    }

}