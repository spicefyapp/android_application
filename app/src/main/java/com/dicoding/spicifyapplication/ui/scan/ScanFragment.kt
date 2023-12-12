package com.dicoding.spicifyapplication.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.spicifyapplication.databinding.FragmentScanBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.helper.getImageUri
import com.dicoding.spicifyapplication.helper.uriToFile

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnAddGalleryPhoto.setOnClickListener { startGallery() }
        binding.btnAddCameraPhoto.setOnClickListener { startCamera() }
        binding.btnPreviewSubmit.setOnClickListener { uploadImageSpice() }

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

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

//    private fun startCameraX() {
//        val intent = Intent(requireContext(), CameraActivity::class.java)
//        launcherIntentCameraX.launch(intent)
//    }

//    private val launcherIntentCameraX = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        if (it.resultCode == CAMERAX_RESULT) {
//            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
//            showImage()
//        }
//    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivImagePreview.setImageURI(it)
        }
    }
    private fun uploadImageSpice() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())
            Log.d("Image File", "showImage: ${imageFile.path}")
//            val description = "Ini adalah deksripsi gambar"
            viewModel.uploadImageSpice(imageFile).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }
                        is ResultState.Success -> {
                            val response = result.data
                            val accuracy = response.accuracy


                            if (accuracy >= 50.toString()) {
                                showAlertDialog(
                                    "Berhasil",
                                    "${response.label}:\n ${response.description} (Akurasi: $accuracy)"
                                )
                            } else {
                                showToast("Akurasi di bawah 50%. Gambar mungkin bukan termasuk rempah. (Akurasi: $accuracy)")
                            }

                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            // Handle OK button click if needed
        }
        builder.create().show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val CROP_RESULT = 101
        const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}