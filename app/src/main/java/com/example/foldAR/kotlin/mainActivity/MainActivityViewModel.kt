package com.example.foldAR.kotlin.mainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.renderer.HelloArRenderer


/**The viewModel handles the renderer as well as the delegation of the calculated data inside
 *  the fragments to the renderer**/
class MainActivityViewModel : ViewModel() {
    private lateinit var _renderer: HelloArRenderer
    val renderer get() = _renderer

    private var _currentPosition: MutableLiveData<Int> = MutableLiveData(0)
    val currentPosition get() = _currentPosition

    private var _scale: MutableLiveData<Float> = MutableLiveData<Float>(1f)
    val scale get() = _scale

    fun setScale(scale: Float) {
        _scale.value = scale
    }

    fun setPosition(position: Int) {
        if (position in renderer.wrappedAnchors.indices)
            _currentPosition.value = position
    }

    fun setRenderer(renderer: HelloArRenderer) {
        _renderer = renderer
    }

    fun changeAnchorsPlaneObject(changeAnchor: ChangeAnchor) {
        renderer.moveAnchorPlane(
            changeAnchor.newX,
            changeAnchor.newZ,
            currentPosition.value!!
        )
    }

    fun changeAnchorsHeight(changeAnchor: ChangeAnchor) =
        renderer.moveAnchorHeight(changeAnchor.newY, 0)

    fun changeAnchorsPlaneCamera(position: Pair<Float, Float>) =
        renderer.moveAnchorPlane(position.first, position.second, currentPosition.value!!)

    //Deletes the object at the specified index and updates the current position.
    fun deleteObject(deletedObjectIndex: Int) {

        if (currentPosition.value == deletedObjectIndex)
            _currentPosition.value = 0
        else if (currentPosition.value!! > deletedObjectIndex)
            _currentPosition.value = currentPosition.value!! - 1

        renderer.deleteAnchor(deletedObjectIndex)
    }
}