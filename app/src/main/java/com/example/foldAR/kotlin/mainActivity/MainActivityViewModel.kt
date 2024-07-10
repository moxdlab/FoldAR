package com.example.foldAR.kotlin.mainActivity

import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.renderer.HelloArRenderer
import com.google.ar.core.Pose


/**The viewModel handles the renderer as well as the delegation of the calculated data inside
 *  the fragments to the renderer**/
class MainActivityViewModel : ViewModel() {
    private var _changeAnchor = ChangeAnchor()
    private val changeAnchor get() = _changeAnchor

    private var pose: Pose? = null

    private lateinit var _renderer: HelloArRenderer
    val renderer get() = _renderer

    private var _currentPosition: MutableLiveData<Int> = MutableLiveData(0)
    val currentPosition get() = _currentPosition

    private var _scale: MutableLiveData<Float> = MutableLiveData<Float>(1f)
    val scale get() = _scale

    private var _touchEvent: MutableLiveData<MotionEvent> = MutableLiveData()
    val touchEvent get() = _touchEvent

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

    fun setTouchEvent(motionEvent: MotionEvent) {
        if (motionEvent.action == MotionEvent.ACTION_MOVE)
            this._touchEvent.value = motionEvent
    }

    fun changeAnchorsPlaneObject(changeAnchor: ChangeAnchor) {
        renderer.moveAnchorPlane(
            changeAnchor.newX,
            changeAnchor.newZ,
            currentPosition.value!!
        )
    }

    //Todo !!!
    fun changeAnchorPosition(view: View, angle: Float) {
        renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
            val scaleFactorY = 500 / view.height
            //Todo check + clean
            if (touchEvent.value!!.action == MotionEvent.ACTION_DOWN)
                _changeAnchor.setOffset(touchEvent.value!!.y * scaleFactorY)
            //Todo

            if (touchEvent.value!!.action == MotionEvent.ACTION_MOVE) {

                _changeAnchor.getNewPosition(
                    touchEvent.value!!,
                    view,
                    pose!!,
                    angle
                )
            }
            changeAnchorsHeight(changeAnchor)
        }
    }


    private fun changeAnchorsHeight(changeAnchor: ChangeAnchor) =
        renderer.moveAnchorHeight(changeAnchor.newY, currentPosition.value!!)

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

    fun setPose() {
        renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
            this.pose = renderer.wrappedAnchors[currentPosition.value!!].anchor.pose
        }
    }
}