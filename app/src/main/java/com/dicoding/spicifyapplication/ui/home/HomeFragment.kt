package com.dicoding.spicifyapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.spicifyapplication.MainViewModel
import com.dicoding.spicifyapplication.databinding.FragmentHomeBinding
import com.dicoding.spicifyapplication.ui.dashboard.spicelib.SpiceLibActivity
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.SpiceMartActivity
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
                binding.tvUserName.text = email
            }
        }

        binding.btnImgSpiceLib.setOnClickListener {
            startActivity(Intent(requireActivity(),SpiceLibActivity::class.java))
        }

        binding.btnImgSpiceMart.setOnClickListener {
            startActivity(Intent(requireActivity(),SpiceMartActivity::class.java))
        }
    }
}