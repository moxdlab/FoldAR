package com.example.foldAR.kotlin.anchorManipulation


import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.View

/**To map it on the coordinate system and use a Bitmap of Size 500, 500*/
class ChangeAnchor {

    /**if a bitmap of different size is used change this value or assign it direct!!*/
    companion object {
        const val offset = 250f //always half of used bitmap. I found that 500 is an acceptable size
        const val Tag = "changeAnchorTag"
    }

    //its bitmap.size/scaleFactor/2 in meters at the views edges
    private var scaleFactor: Int = 500
    private var anchor: FloatArray? = null

    private var distanceX = 0f
    private var distanceZ = 0f
    private var distanceY = 0f

    //returns the new anchors specific value plus moved distance
    val newX get() = anchor?.get(0)?.plus(calculateNewPosition(distanceX)) ?: 0f
    val newY get() = anchor?.get(1)?.minus(calculateNewPosition(distanceY)) ?: 0f
    val newZ get() = anchor?.get(2)?.plus(calculateNewPosition(distanceZ)) ?: 0f

    //to center object in the bitmap and don`t only use values within it
    private fun calculatePoints(value: Float): Float {
        return (value - offset).coerceIn(-offset, offset)
    }

    private fun calculateNewPosition(distance: Float) = distance / scaleFactor

    private fun calculatePointsPlane(x: Float, y: Float) {
        distanceX = calculatePoints(x)
        distanceY = calculatePoints(y)
        distanceZ = calculatePoints(y)
    }

    //get touch value and normalize it to the bitmaps size
    fun getNewPosition(event: MotionEvent, view: View, bitmap: Bitmap, anchorPos: FloatArray, rotation: Float) {
        anchor = anchorPos
        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val touchX = event.x * scaleFactorX
        val touchY = event.y * scaleFactorY

        calculatePointsPlane(touchX, touchY)
    }
}
