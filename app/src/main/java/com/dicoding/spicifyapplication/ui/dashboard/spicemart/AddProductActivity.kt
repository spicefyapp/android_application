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
//        currentImageUri = getImageUri(this)
//        launcherIntentCamera.launch(currentImageUri)

        // Periksa izin kamera sebelum meluncurkan intent kamera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        } else {
            // Jika izin belum diberikan, minta izin kepada pengguna.
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
                    // Izin kamera diberikan, lanjutkan dengan intent kamera.
                    startCamera()
                } else {
                    // Izin kamera tidak diberikan, berikan informasi atau tindakan sesuai kebutuhan.
                    Toast.makeText(this, "Izin kamera tidak diberikan.", Toast.LENGTH_SHORT).show()
                }
            }
            // ... (Tambahkan case untuk requestCode lain jika diperlukan)
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

    //Menampilkan gambar di ImageView
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
        // Ambil data dari input pengguna
        val nama = binding.edAddName.text.toString()
        val harga = binding.edAddPrice.text.toString()
        val wa = binding.edAddWa.text.toString()
        val deskripsi = binding.edAddDesc.text.toString()

        // Cek apakah data yang diperlukan sudah diisi
        if (nama.isEmpty() || harga.isEmpty() || deskripsi.isEmpty() || currentImageUri == null) {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        // Generate nama unik untuk gambar
        val imageName = "${System.currentTimeMillis()}.jpg"

        // Referensi Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        // Upload gambar ke Firebase Storage
        currentImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Dapatkan URL gambar setelah berhasil diunggah
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        // Buat objek ProductSpiceModel dengan URL gambar
                        val productModel = ProductSpiceModel(
                            dataNama = nama,
                            dataHarga = harga,
                            dataWa = wa,
                            dataDes = deskripsi,
                            dataImage = uri.toString(),
                            dataLat = location.latitude,
                            dataLon = location.longitude
                        )

                        // Simpan produk ke Firebase Realtime Database
                        val database = FirebaseDatabase.getInstance().reference
                        val produkRef = database.child("produk")

                        val produkKey = produkRef.push().key

                        produkKey?.let {
                            produkRef.child(it).setValue(productModel)
                        }

                        Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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
                    // Handle jika lokasi tidak tersedia
                    Toast.makeText(
                        this@AddProductActivity,
                        "Lokasi tidak tersedia, Silahkan aktifkan GPS Anda",
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
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
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