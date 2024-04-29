package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class CameraPlaneViewModel : ViewModel() {

    companion object {
        private const val range = 2.5f
        private const val meter = 100
        private const val radius = 5f
        private const val bitmapSize = 500
        private val Tag = "ViewModelTAG"
    }

    private var camPosX = 0f
    private var camPosZ = 0f
    private var rotation: Float = 0f
    private var scaleFactorX: Float = 0f
    private var scaleFactorY: Float = 0f
    private var center = bitmapSize / 2

    private val bitmap: Bitmap =
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 2f
    }

    fun mapAnchors(
        camera: Camera,
        wrappedAnchors: MutableList<WrappedAnchor>,
        refreshAngle: Float,
    ): Bitmap {

        this.rotation = refreshAngle
        this.camPosX = camera.pose.translation[0]
        this.camPosZ = camera.pose.translation[2]

        bitmap.eraseColor(Color.TRANSPARENT)

        wrappedAnchors.forEach {
            val anchorPose = it.anchor.pose
            val (anchorPoseX, anchorPoseZ) = arrayOf(anchorPose.tx(), anchorPose.tz())

            if (isInRange(anchorPoseX, anchorPoseZ, camPosX, camPosZ)) {
                drawPoint(anchorPoseX, anchorPoseZ)
            }
        }

        return bitmap
    }


    private fun drawPoint(
        poseX: Float,
        poseZ: Float,
    ) {

        val canvas = Canvas(bitmap)
        val newX = (poseX - camPosX) * meter
        val newZ = (poseZ - camPosZ) * meter

        val newXRotatedX = cos(rotation) * newX - sin(rotation) * newZ
        val newRotatedZ = sin(rotation) * newX + cos(rotation) * newZ

        canvas.drawCircle(-newXRotatedX + center, -newRotatedZ + center, radius, paint)
    }

    private fun isInRange(poseX: Float, poseZ: Float, camPosX: Float, camPosZ: Float): Boolean {
        val distance = kotlin.math.sqrt((poseX - camPosX).pow(2) + (poseZ - camPosZ).pow(2))
        return distance < range
    }

    //Todo check if anchor == camera is centered
    fun moveAnchors(event: MotionEvent, view: View): Pair<Float, Float> {

        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val pointX = (event.x * scaleFactorX) - 250
        val pointZ = (event.y * scaleFactorY) - 250

        val newX = camPosX + pointX
        val newZ = camPosZ + pointZ

        val x = (cos(rotation) * newX + sin(rotation) * newZ) / 100
        val z = (-sin(rotation) * newX + cos(rotation) * newZ) / 100


        return Pair(x, z)
    }
}