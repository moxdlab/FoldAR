package com.example.foldAR.kotlin.cameraPlane

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.foldAR.kotlin.helloar.R
import com.example.foldAR.kotlin.helloar.databinding.FragmentCameraPlaneBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel

class CameraPlaneFragment : Fragment() {

    private lateinit var viewModelActivity: MainActivityViewModel
    private val viewModel: CameraPlaneViewModel by viewModels()

    private var _binding: FragmentCameraPlaneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraPlaneBinding.inflate(inflater, container, false)
        viewModelActivity = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeFragment()
        setData()
        setObservers()
    }

    private fun setData() {
        viewModel.setData(binding.imageMoveObjectPlane)
    }

    private fun changeFragment() {
        binding.previous.setOnClickListener {
            findNavController().navigate(R.id.action_cameraPlaneFragment_to_objectPlaneFragment)
        }
    }

    private fun setObservers() {
        viewModelActivity.renderer.camera.observe(this.viewLifecycleOwner) {
            binding.imageMoveObjectPlane.setImageBitmap(
                viewModel.mapAnchors(
                    it,
                    viewModelActivity.renderer.wrappedAnchors
                )
            )
        }
    }

}