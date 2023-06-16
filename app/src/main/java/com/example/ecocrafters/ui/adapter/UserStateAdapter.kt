package com.example.ecocrafters.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ecocrafters.ui.user_about.UserAboutFragment
import com.example.ecocrafters.ui.user_comment.UserCommentFragment
import com.example.ecocrafters.ui.user_follower.UserFollowerFragment
import com.example.ecocrafters.ui.user_following.UserFollowingFragment
import com.example.ecocrafters.ui.user_post.UserPostFragment

class UserStateAdapter(activity: AppCompatActivity, private val username: String): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = UserPostFragment.newInstance(username)
            1 -> fragment = UserCommentFragment.newInstance(username)
            2 -> fragment = UserFollowerFragment.newInstance(username)
            3 -> fragment = UserFollowingFragment.newInstance(username)
            4 -> fragment = UserAboutFragment.newInstance(username)
        }
        return fragment as Fragment
    }
}