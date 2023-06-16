package com.example.ecocrafters.ui.create_one

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.TagResponse
import com.example.ecocrafters.databinding.FragmentCreatePostStepOneBinding
import com.example.ecocrafters.ui.camera.CameraActivity
import com.example.ecocrafters.utils.FileUtils
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.io.File


class CreatePostStepOneFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentCreatePostStepOneBinding? = null

    private val viewModel: CreatePostViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostStepOneBinding.inflate(inflater, container, false)
//        val getFile = when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> savedInstanceState?.getSerializable(
//                ARG_SAVED_FILE,
//                File::class.java
//            )
//
//            else -> @Suppress("DEPRECATION") savedInstanceState?.getSerializable(ARG_SAVED_FILE) as File?
//        }
//        val isBackCamera = savedInstanceState?.getBoolean(ARG_SAVED_IS_BACK_CAMERA) ?: true
//        viewModel.setFile(getFile)
//        viewModel.setBackCamera(isBackCamera)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()) {
            requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        startSubscription()
        lifecycleScope.launch {
            viewModel.updateAllTag()
        }

        binding?.apply {
            btnEditPhotoCreatePost.setOnClickListener(this@CreatePostStepOneFragment)
            btnNextCreateOne.setOnClickListener(this@CreatePostStepOneFragment)
            ivThumbnailCreatePost.setOnClickListener(this@CreatePostStepOneFragment)
            inputTagCreatePost.setEndIconOnClickListener {
                Log.d("EndIconClicked", "Clicked")
                if (!edTagCreatePost.text.isNullOrBlank()) {
                    viewModel.addPostTag(edTagCreatePost.text.toString())
                    edTagCreatePost.text.clear()
                }
            }
            edTagCreatePost.setOnItemClickListener { parent, view, position, id ->
                val item = edTagCreatePost.adapter.getItem(position)
                viewModel.addPostTag(item as String)
                edTagCreatePost.text.clear()
            }
            edTagCreatePost.threshold = 2
            edTitleCreatePost.setText(viewModel.titleState.value, TextView.BufferType.EDITABLE)
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.tagListState.collect {
                renderResult(it)
            }
        }
        lifecycleScope.launch {
            viewModel.fileState.collect {
                if (it != null) {
                    val rotatedImage = FileUtils.rotateBitmap(
                        BitmapFactory.decodeFile(it.path),
                        viewModel.isBackCameraState.value
                    )
                    binding?.apply {
                        ivThumbnailCreatePost.setImageBitmap(rotatedImage)
                        tvEmptyThumbnail.isVisible = false
                        tvEmptyThumbnailSupport.isVisible = false
                    }
                } else {
                    binding?.apply {
                        tvEmptyThumbnail.isVisible = true
                        tvEmptyThumbnailSupport.isVisible = true
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.postTagState.collect {
                binding?.apply {
                    if (it.isNotEmpty()) {
                            chipGroupCreatePost.removeAllViews()
                        it.forEach {
                            chipGroupCreatePost.addView(
                                Chip(requireContext()).apply {
                                    text = it
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun renderResult(result: ResultOf<List<TagResponse>>?) {
        when (result) {
            is ResultOf.Success -> {
                binding?.apply {
                    edTagCreatePost.setAdapter(
                        TagAutoCompleteAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            result.data
                        )
                    )
                }
            }

            is ResultOf.Error -> {
                requireContext().showToast(result.error)
            }

            else -> {}
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onClick(v: View?) {
        binding?.apply {
            when (v?.id) {
                btnEditPhotoCreatePost.id -> {
                    startCameraX()
                }

                btnNextCreateOne.id -> {
                    val titleEditable = edTitleCreatePost.text
                    if (titleEditable.isNullOrBlank()) {
                        requireContext().showToast(getString(R.string.judul_post_kosong))
                    } else {
                        viewModel.setTitle(titleEditable.toString())
                        findNavController().navigate(R.id.navigation_create_two)
                    }
                }

                ivThumbnailCreatePost.id -> {
                    startCameraX()
                }
            }
        }
    }

    private fun startCameraX() {
        if (allPermissionsGranted()) {
            val intent = Intent(requireActivity(), CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        } else {
            requireContext().showToast(getString(R.string.tidak_diizinkan))
        }
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

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val getFile = FileUtils.reduceFileImage(myFile as File)

            viewModel.setBackCamera(isBackCamera)
            viewModel.setFile(getFile)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}

private class TagAutoCompleteAdapter(
    context: Context?,
    textViewResourceId: Int,
    private val tagList: List<TagResponse>
) :
    ArrayAdapter<String>(context!!, textViewResourceId), Filterable {

    private var resultList: List<String>? = null
    override fun getCount(): Int {
        return resultList?.size ?: 0
    }

    override fun getItem(index: Int): String {
        return resultList?.get(index) ?: ""
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    resultList = tagList.map {
                        it.tag
                    }.filter {
                        it.contains(constraint.toString())
                    }

                    filterResults.values = resultList
                    filterResults.count = resultList?.size ?: 0
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

        }
    }
}