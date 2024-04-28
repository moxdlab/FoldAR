package com.example.foldAR.kotlin.mainActivity

import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.renderer.HelloArRenderer

class MainActivityViewModel : ViewModel() {
    private lateinit var _renderer: HelloArRenderer
    val renderer get() = _renderer

    var currentPosition = 0

    private var _rotation: Float? = null
    val rotation get() = _rotation

    fun setRenderer(renderer: HelloArRenderer) {
        _renderer = renderer
    }

    fun changeAnchorsPlane(changeAnchor: ChangeAnchor) {
        renderer.moveAnchorPlane(
            changeAnchor.newX,
            changeAnchor.newZ,
            currentPosition
        )
    }

    fun changeAnchorsHeight(changeAnchor: ChangeAnchor) =
        renderer.moveAnchorHeight(changeAnchor.newY, 0)

    fun changeAnchorsPlane1(position: Array<Float>) =
        renderer.moveAnchorPlane(position[0], position[1], currentPosition)

    fun deleteObject(deletedObjectIndex: Int) {
        if (currentPosition == deletedObjectIndex)
            currentPosition = 0
        if (currentPosition > deletedObjectIndex)
            currentPosition--

        renderer.deleteAnchor(deletedObjectIndex)
    }
}