package com.example.foldAR.kotlin.mainActivity

import android.util.Log
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

    private var oldDegree = 0f

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

    fun changeAnchorPosition(view: View) {
        renderer.wrappedAnchors.takeIf { it.isNotEmpty() }?.let {
            val scaleFactorY = 500 / view.height
            val currentTouchEvent = touchEvent.value


            currentTouchEvent?.let {

                when (it.action) {
                    MotionEvent.ACTION_DOWN -> changeAnchor.setOffset(it.y * scaleFactorY)

                    MotionEvent.ACTION_MOVE -> changeAnchor.getNewPosition(it, view, pose!!, null)
                }
            }
            changeAnchorsHeight()
        }
    }

    private fun changeAnchorsHeight() =
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

    fun rotateObject(motionEvent: MotionEvent, currentMain: Float) {

        val rotation = ((motionEvent.getX(0) - currentMain) / 1.47) % 360
        renderer.rotateAnchor(rotation.toFloat(), currentPosition.value!!)
        Log.d("rotationTest", rotation.toString())

    }

    fun resetRotation() {
        oldDegree = 0f;
    }
}