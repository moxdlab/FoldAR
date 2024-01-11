package com.example.foldAR.kotlin.helloar


import android.util.Log

class CalculatePosition {
    private var oldX = 0f
    private var oldZ = 0f

    fun calculatePoints(x: Float, y: Float) {
        val newX = x - 250
        val newY = y - 250


        oldX += (newX - oldX)
        oldZ += (newY - oldZ)
        Log.d("calcLog", "$oldX + $oldZ")

    }


    fun returnValueZ(): Float = oldX

    fun returnValueX(): Float = oldZ
}