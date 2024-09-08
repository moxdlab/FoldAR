package com.example.foldAR.kotlin.anchorManipulation

/**Only used for ObjectPlane since an other approach is used in other fragments as of now.
 * Could directly be implemented in viewModel.*/

import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View
import com.example.foldAR.kotlin.Constants
import com.google.ar.core.Pose
import kotlin.math.cos
import kotlin.math.sin

/**To map it on the coordinate system and use a Bitmap of Size 500, 500*/
class ChangeAnchor {

    /**if a bitmap of different size is used change this value or assign it direct!!*/
    companion object {
        //always half of used bitmap. I found that 500 is an acceptable size
        const val Tag = "changeAnchorTag"
        private val bitmap =
            Bitmap.createBitmap(Constants.bitmapSize, Constants.bitmapSize, Bitmap.Config.ARGB_8888)
    }

    private var offset: Float = 250f

    fun setOffset(offset: Float) {
        this.offset = offset
    }

    //its bitmap.size/scaleFactor/2 in meters at the views edges
    private var scaleFactor: Int = 500
    private var anchor: Pose? = null

    private var rotation = 0f
    private var distanceX = 0f
    private var distanceZ = 0f
    private var distanceY = 0f
    private var x1 = 0f
    private var z1 = 0f

    //returns the new anchors specific value plus moved distance
    val newX get() = anchor?.tx()?.plus(calculateNewPosition(x1)) ?: 0f
    val newY get() = anchor?.ty()?.minus(calculateNewPosition(distanceY)) ?: 0f
    val newZ get() = anchor?.tz()?.minus(calculateNewPosition(z1)) ?: 0f

    //to center object in the bitmap and don`t only use values within it
    private fun calculatePoints(value: Float): Float = (value - offset).coerceIn(-offset, offset)

    private fun calculateNewPosition(distance: Float) = distance / offset

    private fun calculatePointsPlane(x: Float, y: Float) {
        distanceX = -calculatePoints(x)
        distanceY = calculatePoints(y)
        distanceZ = calculatePoints(y)

        x1 = (cos(rotation) * distanceX - sin(rotation) * distanceZ)
        z1 = (sin(rotation) * distanceX + cos(rotation) * distanceZ)
    }

    //get touch value and normalize it to the bitmaps size
    fun getNewPosition(
        event: MotionEvent,
        view: View,
        anchorPos: Pose,
        rotation: Float?
    ) {
        if (rotation != null)
            this.rotation = rotation

        anchor = anchorPos
        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val touchX = event.x * scaleFactorX
        val touchY = event.y * scaleFactorY

        calculatePointsPlane(touchX, touchY)
    }
}
