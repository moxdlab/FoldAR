package com.example.foldAR.kotlin.mainActivity

import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.renderer.HelloArRenderer

class MainActivityViewModel : ViewModel() {

    var anchorPos = 0
    private lateinit var _renderer: HelloArRenderer
    val renderer get() = _renderer

    fun setRenderer(renderer: HelloArRenderer){
        _renderer = renderer
    }

    fun changeAnchorsPlane(changeAnchor: ChangeAnchor) {
        renderer.moveAnchorPlane(changeAnchor.newX, changeAnchor.newZ, 0) //Todo choose position from list
    }

    fun changeAnchorsHeight(changeAnchor: ChangeAnchor) {
        renderer.moveAnchorHeight(changeAnchor.newY, 0)
    }

}