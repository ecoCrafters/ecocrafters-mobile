package com.example.ecocrafters.ui.edit_account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import com.example.ecocrafters.databinding.ActivityEditAccountBinding
import com.example.ecocrafters.ui.camera.CameraActivity
import com.example.ecocrafters.utils.FileUtils
import com.example.ecocrafters.utils.FileUtils.reduceFileImage
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditAccountActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEditAccountBinding
    private var getFile: File? = null
    private var isBackCamera = true
    private val viewModel: EditAccountViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFile = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> savedInstanceState?.getSerializable(
                ARG_SAVED_FILE,
                File::class.java
            )

            else -> @Suppress("DEPRECATION") savedInstanceState?.getSerializable(ARG_SAVED_FILE) as File?
        }
        isBackCamera = savedInstanceState?.getBoolean(ARG_SAVED_IS_BACK_CAMERA) ?: true

        binding.apply {
            setSupportActionBar(toolbarEditAccount)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            btnEditPhotoEdit.setOnClickListener(this@EditAccountActivity)
            btnCancelEdit.setOnClickListener(this@EditAccountActivity)
            btnSaveEdit.setOnClickListener(this@EditAccountActivity)
        }

        setAccountInfo()

        startSubscription()
    }

    private fun setAccountInfo() {
        binding.apply {
            edNameEditAccount.setText(intent.getStringExtra(ARG_FULLNAME))
            edUsernameEditAccount.setText(intent.getStringExtra(ARG_USERNAME))
            if (getFile == null) {
                ivPhotoEditAccount.loadRectImage(intent.getStringExtra(ARG_AVATAR))
            } else {
                val rotatedImage = FileUtils.rotateBitmap(
                    BitmapFactory.decodeFile(getFile?.path),
                    isBackCamera
                )
                ivPhotoEditAccount.setImageBitmap(rotatedImage)
            }
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editAccountState.collect {
                    renderResult(it)
                }
            }
        }
    }

    private fun renderResult(result: ResultOf<UserProfileResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                lifecycleScope.launch {
                    result.data.apply {
                        viewModel.saveAccountProfile(email, fullName, username, avatar, ecoPoints)
                        finish()
                    }
                }
            }

            is ResultOf.Error -> {
                showLoading(false)
                showToast(result.error)
                Log.d("This", result.error)
            }

            else -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            overlayEditAccount.isVisible = isLoading
            pbEditAccount.isVisible = isLoading
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                btnEditPhotoEdit.id -> {
                    Log.d("this", "Clicked")
                    if (!allPermissionsGranted()) {
                        ActivityCompat.requestPermissions(
                            this@EditAccountActivity,
                            REQUIRED_PERMISSIONS,
                            REQUEST_CODE_PERMISSIONS
                        )
                    } else {
                        startCameraX()
                    }
                }

                btnSaveEdit.id -> {
                    uploadImage()
                }

                btnCancelEdit.id -> {
                    finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        if (allPermissionsGranted()) {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        } else {
            showToast(getString(R.string.tidak_diizinkan))
        }
    }

    private fun uploadImage() {
        if (isInputInvalid()) {
            return
        }
        val imageFile = getFile

        val imageMultipart: MultipartBody.Part? =
            if (imageFile != null) {
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "avatar",
                    imageFile.name,
                    requestImageFile
                )
            } else {
                null
            }

        binding.apply {
            val fullNameBody = edNameEditAccount
                .text
                .toString()
                .toRequestBody("text/plain".toMediaType())
            val usernameBody = edUsernameEditAccount
                .text
                .toString()
                .toRequestBody("text/plain".toMediaType())
            viewModel.editAccountProfile(
                fullNameBody,
                usernameBody,
                imageMultipart
            )
        }


    }

    private fun isInputInvalid(): Boolean {
        binding.apply {
            val isInputEmpty = edUsernameEditAccount.text.isNullOrBlank()
                    || edNameEditAccount.text.isNullOrBlank()
            val isInputError = edUsernameEditAccount.error != null
                    || edNameEditAccount.error != null
            Log.d("edUsernameEditAccount", (edUsernameEditAccount.error != null).toString())
            Log.d("edNameEditAccount", (edNameEditAccount.error != null).toString())

            return when {
                isInputEmpty -> {
                    showToast(getString(R.string.salah_satu_input_kosong))
                    true
                }

                isInputError -> {
                    showToast(getString(R.string.salah_satu_input_tidak_valid))
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_SAVED_FILE, getFile)
        outState.putBoolean(ARG_SAVED_IS_BACK_CAMERA, isBackCamera)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {
            val myFile = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> it.data?.getSerializableExtra(
                    "picture",
                    File::class.java
                )

                else -> @Suppress("DEPRECATION") it.data?.getSerializableExtra("picture") as File
            }

            isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = reduceFileImage(myFile as File)
            val rotatedImage = FileUtils.rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ivPhotoEditAccount.setImageBitmap(rotatedImage)
        }
    }

    companion object {

        const val ARG_USERNAME = "username"
        const val ARG_FULLNAME = "full_name"
        const val ARG_AVATAR = "avatar"

        private const val ARG_SAVED_FILE = "image_avatar_get_file"
        private const val ARG_SAVED_IS_BACK_CAMERA = "is_back_camera"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}