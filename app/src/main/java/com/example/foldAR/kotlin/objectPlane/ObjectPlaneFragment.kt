package com.example.foldAR.kotlin.objectPlane

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.helloar.databinding.FragmentObjectPlaneBinding
import com.example.foldAR.kotlin.mainActivity.MainActivityViewModel

class ObjectPlaneFragment : Fragment() {

    private val viewModelActivity: MainActivityViewModel by activityViewModels()
    private val viewModel: ObjectPlaneViewModel by viewModels()

    private var _binding: FragmentObjectPlaneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentObjectPlaneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMoveObjectListeners()
    }


    private fun setUpMoveObjectListeners() {
        selectMovementMethod(
            binding.imageMoveObjectPlane,
            viewModelActivity::changeAnchorsPlaneObject
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
                                .getAnchorPosition(viewModelActivity.currentPosition.value!!)
                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        viewModel.changeAnchorPosition(
                            event,
                            view,
                            viewModelActivity.renderer.refreshAngle()
                        )
                        action2(viewModel.changeAnchor)
                    }
                }
            }
            view.performClick()
            true

        }
    }
}