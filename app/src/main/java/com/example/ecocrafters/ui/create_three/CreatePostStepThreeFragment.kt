package com.example.ecocrafters.ui.create_three

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CreatePostResponse
import com.example.ecocrafters.databinding.FragmentCreatePostStepThreeBinding
import com.example.ecocrafters.ui.create_one.CreatePostViewModel
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch


class CreatePostStepThreeFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentCreatePostStepThreeBinding? = null
    private val viewModel: CreatePostViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostStepThreeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSubscription()

        binding?.apply {
            val wysiwygEditor = editor
            wysiwygEditor.setEditorHeight(200)
            wysiwygEditor.setEditorFontSize(16)
            wysiwygEditor.setPadding(10, 10, 10, 10)
            wysiwygEditor.setPlaceholder(getString(R.string.masukkan_konten_post_disini))
            wysiwygEditor.html = viewModel.contentState.value


            actionUndo.setOnClickListener { wysiwygEditor.undo() }

            actionRedo.setOnClickListener { wysiwygEditor.redo() }

            actionBold.setOnClickListener { wysiwygEditor.setBold() }

            actionItalic.setOnClickListener { wysiwygEditor.setItalic() }

            actionSubscript.setOnClickListener { wysiwygEditor.setSubscript() }

            actionSuperscript.setOnClickListener { wysiwygEditor.setSuperscript() }

            actionStrikethrough.setOnClickListener { wysiwygEditor.setStrikeThrough() }

            actionUnderline.setOnClickListener { wysiwygEditor.setUnderline() }

            actionHeading1.setOnClickListener {
                wysiwygEditor.setHeading(
                    1
                )
            }

            actionHeading2.setOnClickListener {
                wysiwygEditor.setHeading(
                    2
                )
            }

            actionHeading3.setOnClickListener {
                wysiwygEditor.setHeading(
                    3
                )
            }

            actionHeading4.setOnClickListener {
                wysiwygEditor.setHeading(
                    4
                )
            }

            actionHeading5.setOnClickListener {
                wysiwygEditor.setHeading(
                    5
                )
            }

            actionHeading6.setOnClickListener {
                wysiwygEditor.setHeading(
                    6
                )
            }

            actionIndent.setOnClickListener { wysiwygEditor.setIndent() }

            actionOutdent.setOnClickListener { wysiwygEditor.setOutdent() }

            actionAlignLeft.setOnClickListener { wysiwygEditor.setAlignLeft() }

            actionAlignCenter.setOnClickListener { wysiwygEditor.setAlignCenter() }

            actionAlignRight.setOnClickListener { wysiwygEditor.setAlignRight() }

            actionAlignJustify.setOnClickListener { wysiwygEditor.setAlignJustifyFull() }

            actionInsertBullets.setOnClickListener { wysiwygEditor.setBullets() }

            actionInsertNumbers.setOnClickListener { wysiwygEditor.setNumbers() }

            actionInsertCheckbox.setOnClickListener { wysiwygEditor.insertTodo() }

            var visible = false

            preview.setOnClickListener {
                if (!visible) {
                    wysiwygEditor.setInputEnabled(false)
                    preview.setImageResource(com.github.onecode369.wysiwyg.R.drawable.visibility_off)
                } else {
                    wysiwygEditor.setInputEnabled(true)
                    preview.setImageResource(com.github.onecode369.wysiwyg.R.drawable.visibility)
                }
                visible = !visible
            }

            insertLatex.setOnClickListener {
                if (latextEditor.visibility == View.GONE) {
                    latextEditor.visibility = View.VISIBLE
                    submitLatex.setOnClickListener {
                        wysiwygEditor.insertLatex(latexEquation.text.toString())
                    }
                } else {
                    latextEditor.visibility = View.GONE
                }
            }

            insertCode.setOnClickListener { wysiwygEditor.setCode() }

            btnBackCreatePost.setOnClickListener(this@CreatePostStepThreeFragment)
            btnNextCreatePost.setOnClickListener(this@CreatePostStepThreeFragment)
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.createPostState.collect{
                renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<CreatePostResponse>?) {
        when(result){
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Error -> {
                showLoading(false)
                requireContext().showToast(result.error)
            }
            is ResultOf.Success -> {
                viewModel.resetViewModel()
                showLoading(false)
                requireContext().showToast(getString(R.string.kirim_post_berhasil))
                findNavController().popBackStack(R.id.navigation_create, false)
                val intent = Intent(requireActivity(), PostActivity::class.java).apply {
                    putExtra(PostActivity.ARG_SLUG, result.data.slug)
                    putExtra(PostActivity.ARG_POST_ID, result.data.id)
                }
                startActivity(intent)
            }
            null -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            overlayCreatePost.isVisible = isLoading
            pbCreatePost.isVisible = isLoading
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.apply {
            viewModel.updateContent(editor.html ?: "")
        }
    }

    override fun onClick(v: View?) {
        binding?.apply {
            when(v?.id){
                btnBackCreatePost.id -> {
                    findNavController().popBackStack()
                }
                btnNextCreatePost.id -> {
                    if (editor.html != null) {
                        viewModel.updateContent(editor.html)
                        viewModel.createPost()
                    } else {
                        requireContext().showToast(getString(R.string.konten_kosong))
                    }
                }
            }
        }
    }

}