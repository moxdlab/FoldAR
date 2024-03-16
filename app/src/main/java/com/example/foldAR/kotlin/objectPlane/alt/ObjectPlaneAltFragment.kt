package com.example.foldAR.kotlin.objectPlane.alt

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.foldAR.kotlin.helloar.R
import com.example.foldAR.kotlin.helloar.databinding.FragmentObjectPlaneAltBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel
import com.example.foldAR.kotlin.objectPlane.ObjectPlaneViewModel

class ObjectPlaneAltFragment : Fragment() {

    private lateinit var viewModelActivity: MainActivityViewModel
    private val viewModel: ObjectPlaneViewModel by viewModels()
    private val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    private var _binding: FragmentObjectPlaneAltBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentObjectPlaneAltBinding.inflate(inflater, container, false)
        viewModelActivity = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMoveObjectListeners()
        changeFragment()
    }

    private fun changeFragment() {
        binding.next.setOnClickListener {
            findNavController().navigate(R.id.action_objectPlaneAltFragment_to_objectPlaneFragment)
        }
    }

    private fun setUpMoveObjectListeners() {
        var multi = false
        binding.imageMoveObjectPlane.apply {
            setOnTouchListener { view, event ->
                viewModelActivity.renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.setAnchorsPos(
                                viewModelActivity.renderer.getAnchorPosition(viewModelActivity.anchorPos)
                            )
                        }

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            multi = true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            if (!multi) {
                               //viewModel.changeAnchorsPlane(event, view, bitmap)
                               //viewModelActivity.changeAnchorsPlane(viewModel.changeAnchor)
                                Log.e(TAG, multi.toString())
                            }
                            if (multi) {
                                //viewModel.changeAnchorsHeight(event, view, bitmap)
                                //viewModelActivity.changeAnchorsHeight(viewModel.changeAnchor)
                                Log.e(TAG, multi.toString())
                            }
                        }

                        MotionEvent.ACTION_POINTER_UP -> {
                            multi = false
                        }

                    }
                }
                view.performClick()
                true
            }
        }
    }
    companion object{
        const val TAG = "ObjectPlaneAltFragmentTest"
    }
}