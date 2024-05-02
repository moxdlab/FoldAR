package com.example.foldAR.kotlin.objectPlane

import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.google.ar.core.Pose

class ObjectPlaneViewModel : ViewModel() {

    private val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    private var _changeAnchor = ChangeAnchor()
    val changeAnchor get() = _changeAnchor

    private var anchorPos: Pose? = null

    fun changeAnchorPosition(event: MotionEvent, view: View, angle: Float) {
        _changeAnchor.getNewPosition(event, view, anchorPos!!, angle)
    }

    fun setAnchorsPos(anchorPosition: Pose) {
        anchorPos = anchorPosition
    }


}