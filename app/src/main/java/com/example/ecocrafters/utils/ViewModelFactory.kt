package com.example.ecocrafters.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.di.Injection
import com.example.ecocrafters.ui.account_security.AccountSecurityViewModel
import com.example.ecocrafters.ui.change_password.ChangePasswordViewModel
import com.example.ecocrafters.ui.check_code.CheckCodeViewModel
import com.example.ecocrafters.ui.create_one.CreatePostViewModel
import com.example.ecocrafters.ui.edit_account.EditAccountViewModel
import com.example.ecocrafters.ui.home.HomeViewModel
import com.example.ecocrafters.ui.login.LoginViewModel
import com.example.ecocrafters.ui.main.MainViewModel
import com.example.ecocrafters.ui.more.MoreViewModel
import com.example.ecocrafters.ui.myaccount.MyAccountViewModel
import com.example.ecocrafters.ui.post.PostViewModel
import com.example.ecocrafters.ui.register.RegisterViewModel
import com.example.ecocrafters.ui.saved_post.SavedPostViewModel
import com.example.ecocrafters.ui.scan_result.ScanResultViewModel
import com.example.ecocrafters.ui.search_post.SearchPostViewModel
import com.example.ecocrafters.ui.search_user.SearchUserViewModel
import com.example.ecocrafters.ui.starting.StartingViewModel
import com.example.ecocrafters.ui.user.UserViewModel
import com.example.ecocrafters.ui.user_about.UserAboutViewModel
import com.example.ecocrafters.ui.user_comment.UserCommentViewModel
import com.example.ecocrafters.ui.user_follower.UserFollowerViewModel
import com.example.ecocrafters.ui.user_following.UserFollowingViewModel
import com.example.ecocrafters.ui.user_post.UserPostViewModel

class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(CheckCodeViewModel::class.java) -> {
                CheckCodeViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) -> {
                ChangePasswordViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(AccountSecurityViewModel::class.java) -> {
                AccountSecurityViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(StartingViewModel::class.java) -> {
                StartingViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(MoreViewModel::class.java) -> {
                MoreViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(MyAccountViewModel::class.java) -> {
                MyAccountViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserPostViewModel::class.java) -> {
                UserPostViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserCommentViewModel::class.java) -> {
                UserCommentViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserFollowingViewModel::class.java) -> {
                UserFollowingViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserFollowerViewModel::class.java) -> {
                UserFollowerViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserAboutViewModel::class.java) -> {
                UserAboutViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SearchUserViewModel::class.java) -> {
                SearchUserViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(EditAccountViewModel::class.java) -> {
                EditAccountViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SearchPostViewModel::class.java) -> {
                SearchPostViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SavedPostViewModel::class.java) -> {
                SavedPostViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(CreatePostViewModel::class.java) -> {
                CreatePostViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(ScanResultViewModel::class.java) -> {
                ScanResultViewModel(userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it }
    }

}