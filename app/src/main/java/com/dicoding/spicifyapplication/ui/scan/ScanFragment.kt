package com.dicoding.spicifyapplication.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.databinding.FragmentScanBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.helper.ViewModelFactory
import com.dicoding.spicifyapplication.helper.getImageUri
import com.dicoding.spicifyapplication.helper.reduceFileImage
import com.dicoding.spicifyapplication.helper.uriToFile
import com.dicoding.spicifyapplication.ui.scan.detailscan.DetailScanActivity

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


    @RequiresApi(Build.VERSION_CODES.Q)
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
            Log.d(TAG, "No media selected")
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
    private fun showImage() {
        currentImageUri?.let {
            Log.d(TAG, "showImage: $it")
            binding.ivImagePreview.setImageURI(it)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadImageSpice() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d(TAG, "showImage: ${imageFile.path}")
            viewModel.uploadImageSpice(imageFile).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }
                        is ResultState.Success -> {
                            val response = result.data
                            val accuracy = response.accuracy
                            val cleanedAccuracy = accuracy.replace("%", "")
                            if (cleanedAccuracy.toFloat() >= 75) {
                                val intent = Intent(requireContext(), DetailScanActivity::class.java).apply {
                                    putExtra(DetailScanActivity.EXTRA_IMAGE_URI, uri.toString())
                                    putExtra(DetailScanActivity.EXTRA_RESPONSE_LABEL, response.label)
                                    putExtra(DetailScanActivity.EXTRA_RESPONSE_DESCRIPTION, response.description)
                                    putExtra(DetailScanActivity.EXTRA_RESPONSE_ACCURACY, accuracy)
                                }
                                startActivity(intent)
//                                activity?.finish()
                            } else {
                                showToast(getString(R.string.sorry_not_spices))
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
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ScanFragment"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}