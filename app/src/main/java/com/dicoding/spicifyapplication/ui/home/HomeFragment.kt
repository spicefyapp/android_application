package com.dicoding.spicifyapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.spicifyapplication.MainViewModel
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.databinding.FragmentHomeBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpices
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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpiceLib.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvSpiceLib.addItemDecoration(itemDecoration)

        setupData()
    }

    private fun setupData() {
        viewModel.getSession().observe(requireActivity()) { user ->
            if (user.token.isNotBlank()) {
                processGetAllSpices()
            }
        }
    }

    private fun processGetAllSpices() {
        viewModel.getStories().observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
//                        binding.progressBar.visibility = View.GONE
                        setListStory(result.data!!)
                    }
                    is ResultState.Error -> {
//                        Toast.makeText(
//                            this,
//                            R.string.failed_to_load_data,
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setListStory(listSpice: List<RempahItem?>) {
        val adapter = AdapterSpices()
        adapter.submitList(listSpice)
        binding.rvSpiceLib.adapter = adapter
    }
}