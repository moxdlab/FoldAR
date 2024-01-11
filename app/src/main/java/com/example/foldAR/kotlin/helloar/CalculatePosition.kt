package com.example.foldAR.kotlin.helloar


import android.util.Log

class CalculatePosition {
    private var oldX = 0f
    private var oldZ = 0f
    private var oldY = 0f

    fun calculatePointsPlane(x: Float, y: Float) {
        val newX = x - 250
        val newY = y - 250

        oldX += (newX - oldX)
        oldZ += (newY - oldZ)
        Log.d("calcLog", "$oldX + $oldZ")
    }

    fun calculatePointsHeight(y: Float) {
        val newY = y - 250
        oldY += (newY - oldY)
    }


    fun returnValueZ(): Float = oldX.coerceIn(-250f, 250f)

    fun returnValueX(): Float = oldZ.coerceIn(-250f, 250f)

    fun returnValueY(): Float = -oldY.coerceIn(-250f, 250f)

}