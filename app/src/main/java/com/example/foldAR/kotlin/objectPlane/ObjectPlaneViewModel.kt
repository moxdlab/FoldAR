package com.example.foldAR.kotlin.objectPlane

import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor

class ObjectPlaneViewModel : ViewModel(){

    private val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    private var _changeAnchor = ChangeAnchor()
    val changeAnchor get() = _changeAnchor

    private var anchorPos: FloatArray = FloatArray(3)

    fun changeAnchorsPlane(event: MotionEvent, view: View) {
        _changeAnchor.getNewPosition(event, view, bitmap, anchorPos)
    }

    fun changeAnchorsHeight(event: MotionEvent, view: View) {
        _changeAnchor.getNewPosition(event, view, bitmap, anchorPos)
    }

    fun setAnchorsPos(anchorPosition: FloatArray) {
        anchorPos = anchorPosition
    }

}