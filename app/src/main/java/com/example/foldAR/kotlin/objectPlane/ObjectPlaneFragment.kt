package com.example.foldAR.kotlin.objectPlane

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.helloar.databinding.FragmentObjectPlaneBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel

class ObjectPlaneFragment : Fragment() {

    private lateinit var viewModelActivity: MainActivityViewModel
    private val viewModel: ObjectPlaneViewModel by viewModels()
    private val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    private var _binding: FragmentObjectPlaneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentObjectPlaneBinding.inflate(inflater, container, false)
        viewModelActivity = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMoveObjectListeners()
        changeFragment()


    }

    private fun changeFragment(){
        binding.root.setOnClickListener {
            Toast.makeText(requireActivity(), "Click!", Toast.LENGTH_LONG).show()
        }
    }


    private fun setUpMoveObjectListeners() {
        selectMovementMethod(
            binding.imageMoveObjectPlane,
            viewModel::changeAnchorsPlane,
            viewModelActivity::changeAnchorsPlane
        )

        selectMovementMethod(
            binding.imageMoveObjectHeight,
            viewModel::changeAnchorsHeight,
            viewModelActivity::changeAnchorsHeight
        )
    }

    private fun selectMovementMethod(
        viewImage: View,
        action1: (MotionEvent, View, Bitmap) -> Unit,
        action2: (ChangeAnchor) -> Unit,
    ) {
        viewImage.setOnTouchListener { view, event ->
            viewModelActivity.renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.setAnchorsPos(
                            viewModelActivity
                                .renderer
                                .getAnchorPosition(viewModelActivity.anchorPos)!!
                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        action1(event, view, bitmap)
                        action2(viewModel.changeAnchor)
                    }
                }
            }
            view.performClick()
            true

        }
    }
}