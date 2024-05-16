package com.example.foldAR.kotlin.objectPlane

import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.google.ar.core.Pose

class ObjectPlaneViewModel : ViewModel() {

    private var _changeAnchor = ChangeAnchor()
    val changeAnchor get() = _changeAnchor

    private var anchorPose: Pose? = null

    fun changeAnchorPosition(event: MotionEvent, view: View, angle: Float) {
        _changeAnchor.getNewPosition(event, view, anchorPose!!, angle)
    }

    fun setAnchorsPos(anchorPosition: Pose) {
        anchorPose = anchorPosition
    }
}