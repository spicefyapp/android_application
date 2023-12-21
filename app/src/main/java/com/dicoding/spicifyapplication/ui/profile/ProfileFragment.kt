package com.dicoding.spicifyapplication.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.spicifyapplication.MainViewModel
import com.dicoding.spicifyapplication.R
import com.dicoding.spicifyapplication.databinding.FragmentProfileBinding
import com.dicoding.spicifyapplication.ui.profile.about.AboutActivity
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(requireActivity()) { user ->
            if (user.token.isNotBlank()) {
                val email = user.email
                binding.tvEmailProfile.text = email
            }
        }
        binding.btnCvAbout.setOnClickListener {
            startActivity(Intent(requireContext(),AboutActivity::class.java))
        }

        binding.btnCvLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.btnLogout.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.apply {
                setTitle(getString(R.string.confirm_logout))
                setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deleteLogin()
                }
                setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

}