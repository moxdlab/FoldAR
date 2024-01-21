package com.example.foldAR.kotlin.helloar


import android.graphics.Bitmap
import android.util.Log
import android.view.MotionEvent
import android.view.View

//To map it on the coordinate system and use a Bitmap of Size 500, 500
class ChangeAnchor {
    private var scaleFactor: Int = 2000
    private lateinit var anchor: FloatArray

    fun setScaleFactor(factor: Int) {
        scaleFactor = factor
    }

    private var oldX = 0f
    private var oldZ = 0f
    private var oldY = 0f

    val newX get() = anchor[0] + calculateNewPosition(oldX)
    val newY get() = anchor[1] - calculateNewPosition(oldY)
    val newZ get() = anchor[2] + calculateNewPosition(oldZ)

    private fun calculatePoints(value: Float): Float {
        return (value - 250).coerceIn(-250f, 250f)
    }

    private fun calculateNewPosition(old: Float) = old / scaleFactor

    private fun calculatePointsPlane(x: Float, y: Float) {
        oldX = calculatePoints(x)
        oldZ = calculatePoints(y)
        Log.d("calcLog", "$oldX + $oldZ")
    }

    private fun calculatePointsHeight(y: Float) {
        oldY = calculatePoints(y)
    }

    fun getNewPosition(event: MotionEvent, view: View, bitmap: Bitmap, anchorPos: FloatArray) {
        anchor = anchorPos
        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val touchX = event.x * scaleFactorX
        val touchY = event.y * scaleFactorY

        calculatePointsPlane(touchX, touchY)
        calculatePointsHeight(touchY)
    }
}
