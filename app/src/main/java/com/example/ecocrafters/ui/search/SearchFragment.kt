package com.example.ecocrafters.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.ecocrafters.R
import com.example.ecocrafters.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : Fragment(){

    private var binding: FragmentSearchBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        binding?.apply {
            if (edSearchSearch.text.toString().isNotBlank()) {
                inputSearchSearch.hint = null
            } else {
                inputSearchSearch.hint = getString(R.string.cari_post_atau_pengguna)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            edSearchSearch.setOnFocusChangeListener { _, hasFocus ->
                vpSearch.isVisible = hasFocus
                tlSearch.isVisible = hasFocus
                inputSearchSearch.hint = null
            }
            edSearchSearch.setOnEditorActionListener{ _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edSearchSearch.let { view ->
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    val searchAdapter = SearchStateAdapter(
                        requireActivity() as AppCompatActivity,
                        edSearchSearch.text.toString()
                    )
                    val vpIndex = vpSearch.currentItem
                    vpSearch.adapter = searchAdapter
                    vpSearch.currentItem = vpIndex
                }
                true
            }
        }
        loadTabLayout()
    }

    private fun loadTabLayout() {
        binding?.apply {
            val searchAdapter = SearchStateAdapter(
                requireActivity() as AppCompatActivity,
                edSearchSearch.text.toString()
            )
            val viewPager: ViewPager2 = vpSearch
            viewPager.adapter = searchAdapter
            val tabs: TabLayout = tlSearch
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
        }
    }

    companion object {
        private val TAB_TITLES = intArrayOf(
            R.string.post,
            R.string.pengguna
        )
    }

}