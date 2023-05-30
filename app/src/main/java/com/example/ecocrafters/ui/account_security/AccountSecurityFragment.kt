package com.example.ecocrafters.ui.account_security

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecocrafters.databinding.FragmentAccountSecurityBinding
import com.example.ecocrafters.ui.change_password.CheckCodeActivity

class AccountSecurityFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentAccountSecurityBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSecurityBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            cvChangePassword.setOnClickListener(this@AccountSecurityFragment)
        }
    }

    override fun onClick(v: View) {
        binding?.apply {
            when (v.id) {
                cvChangePassword.id -> {
                    val intent = Intent(activity, CheckCodeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}