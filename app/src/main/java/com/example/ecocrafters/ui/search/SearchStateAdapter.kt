package com.example.ecocrafters.ui.search

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ecocrafters.ui.search_post.SearchPostFragment
import com.example.ecocrafters.ui.search_user.SearchUserFragment

class SearchStateAdapter (activity: AppCompatActivity, private val searchTerm: String): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SearchPostFragment.newInstance(searchTerm)
            1 -> fragment = SearchUserFragment.newInstance(searchTerm)
        }
        return fragment as Fragment
    }
}
