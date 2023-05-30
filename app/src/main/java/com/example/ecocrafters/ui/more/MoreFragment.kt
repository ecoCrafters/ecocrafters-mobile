package com.example.ecocrafters.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecocrafters.databinding.FragmentMoreBinding
import com.example.ecocrafters.ui.account_security.AccountSecurityFragment

class MoreFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentMoreBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            cvSecurityMore.setOnClickListener(this@MoreFragment)
        }
    }

    override fun onClick(v: View) {
        binding?.apply {
            when (v.id) {
                cvSecurityMore.id -> {
                    parentFragmentManager.beginTransaction()
                        .add(AccountSecurityFragment(), "account_security").commit()
                }
            }
        }
    }
}