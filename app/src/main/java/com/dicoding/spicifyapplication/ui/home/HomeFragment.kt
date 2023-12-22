package com.dicoding.spicifyapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.spicifyapplication.MainViewModel
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.databinding.FragmentHomeBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.adapter.AdapterSpices
import com.dicoding.spicifyapplication.ui.dashboard.spicelib.SpiceLibActivity
import com.dicoding.spicifyapplication.ui.dashboard.spiceloc.MapsActivity
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.SpiceMartActivity
import com.dicoding.spicifyapplication.helper.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpiceLib.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvSpiceLib.addItemDecoration(itemDecoration)

        setupListener()
        setupData()
        setupSearchView()
        setupDoublePressBack()

    }

    private fun setupListener() {
        binding.btnImgSpiceLib.setOnClickListener {
            startActivity(Intent(requireActivity(),SpiceLibActivity::class.java))
        }

        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(requireActivity(),SpiceLibActivity::class.java))
        }

        binding.btnImgSpiceLoc.setOnClickListener {
            startActivity(Intent(requireActivity(),MapsActivity::class.java))
        }

        binding.btnImgSpiceMart.setOnClickListener {
            startActivity(Intent(requireActivity(),SpiceMartActivity::class.java))
        }
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
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        setListStory(result.data!!)
                        showLoading(false)

                    }
                    is ResultState.Error -> {
                        showLoading(false)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchSpices(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    processGetAllSpices()
                }
                return true
            }
        })
    }

    private fun searchSpices(name: String) {
        viewModel.searchSpices(name).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        setListStory(result.data!!)
                        showLoading(false)

                    }
                    is ResultState.Error -> {
                        showLoading(false)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setListStory(listSpice: List<RempahItem?>) {
        val adapter = AdapterSpices()
        adapter.submitList(listSpice)
        binding.rvSpiceLib.adapter = adapter
    }

    private fun setupDoublePressBack(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (doubleBackToExitPressedOnce) {
                requireActivity().finish()
            }

            doubleBackToExitPressedOnce = true
            Toast.makeText(
                requireContext(),
                "Tekan sekali lagi untuk keluar",
                Toast.LENGTH_SHORT
            ).show()

            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}