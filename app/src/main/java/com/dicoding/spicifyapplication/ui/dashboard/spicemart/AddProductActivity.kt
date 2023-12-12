package com.dicoding.spicifyapplication.ui.dashboard.spicemart

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.spicifyapplication.databinding.ActivityAddProductBinding
import com.dicoding.spicifyapplication.helper.getImageUri

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.ivAddImage.setOnClickListener {
            showImageDialog()
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

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 123
    }

}