package com.example.ecocrafters.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecocrafters.databinding.FragmentScanTrashBinding
import com.example.ecocrafters.ui.scan_result.ScanResultActivity
import com.example.ecocrafters.utils.FileUtils
import com.example.ecocrafters.utils.FileUtils.reduceFileImage
import com.example.ecocrafters.utils.FileUtils.uriToFile
import com.example.ecocrafters.utils.showToast


class ScanTrashFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentScanTrashBinding? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val image = result.data?.data as Uri

            val imageFile = uriToFile(image, requireContext())
            val reducedImageFile = reduceFileImage(imageFile, 100000)

            val intent = Intent(requireContext(), ScanResultActivity::class.java)
            intent.putExtra(ScanResultActivity.ARG_IMAGE_CAPTURED, reducedImageFile)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanTrashBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()){
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding?.apply {
            ivCaptureImage.setOnClickListener(this@ScanTrashFragment)
            ivOpenGallery.setOnClickListener(this@ScanTrashFragment)
            ivSwitchCamera.setOnClickListener(this@ScanTrashFragment)
        }
    }


    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                requireContext().showToast("Gagal memunculkan kamera.")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = FileUtils.createFile(requireActivity().application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    requireContext().showToast("Gagal mengambil gambar.")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val reducedImageFile = reduceFileImage(photoFile, 100000)
                    val intent = Intent(requireContext(), ScanResultActivity::class.java)
                    intent.putExtra(ScanResultActivity.ARG_IMAGE_CAPTURED, reducedImageFile)
                    startActivity(intent)
                }
            }
        )
    }

    private fun startGalleryChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val galleryChooser = Intent.createChooser(intent, "Pilih Foto Dari Galeri")
        launcherIntentGallery.launch(galleryChooser)
    }


    override fun onClick(v: View) {
        binding?.apply {
            when(v.id){
                ivCaptureImage.id -> {
                    takePhoto()
                }
                ivSwitchCamera.id -> {
                    cameraSelector =
                        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                    startCamera()
                }
                ivOpenGallery.id -> {
                    startGalleryChooser()
                }
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}