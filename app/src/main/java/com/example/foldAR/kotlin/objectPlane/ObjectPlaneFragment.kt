package com.example.foldAR.kotlin.objectPlane

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.helloar.databinding.FragmentObjectPlaneBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel

class ObjectPlaneFragment : Fragment() {

    private lateinit var viewModelActivity: MainActivityViewModel
    private val viewModel: ObjectPlaneViewModel by viewModels()

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
    }


    private fun setUpMoveObjectListeners() {
        selectMovementMethod(
            binding.imageMoveObjectPlane,
            viewModelActivity::changeAnchorsPlane
        )

        selectMovementMethod(
            binding.imageMoveObjectHeight,
            viewModelActivity::changeAnchorsHeight
        )
    }

    private fun selectMovementMethod(
        viewImage: View,
        action2: (ChangeAnchor) -> Unit,
    ) {
        viewImage.setOnTouchListener { view, event ->
            viewModelActivity.renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.setAnchorsPos(
                            viewModelActivity
                                .renderer
                                .getAnchorPosition(viewModelActivity.anchorPos)
                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        viewModel.changeAnchorPosition(event, view, viewModelActivity.renderer.refreshAngle())
                        action2(viewModel.changeAnchor)
                    }
                }
            }
            view.performClick()
            true

        }
    }
}