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
        private const val meter = 100
        private const val radius = 5f
        private const val bitmapSize = 500
        private val Tag = "ViewModelTAG"
    }

    private var _range = 2.5f
    private val range get() = _range

    private var camPosX = 0f
    private var camPosZ = 0f
    private var rotation: Float = 0f
    private var center = bitmapSize / 2

    private val bitmap: Bitmap =
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 2f
    }

    fun setRange(range: Float) {
        if (range in 1.0..5.0)
            _range = range
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
        val newX = (poseX - camPosX) * (meter)
        val newZ = (poseZ - camPosZ) * (meter)

//        1     = / 0.4
//        2.5   = / 1
//        5     = / 2
        val newXRotatedX = cos(rotation) * newX - sin(rotation) * newZ
        val newRotatedZ = sin(rotation) * newX + cos(rotation) * newZ

        canvas.drawCircle(
            (-newXRotatedX + center).toFloat(),
            (-newRotatedZ + center).toFloat(), radius, paint
        )
    }

    private fun isInRange(poseX: Float, poseZ: Float, camPosX: Float, camPosZ: Float): Boolean {
        val distance = kotlin.math.sqrt((poseX - camPosX).pow(2) + (poseZ - camPosZ).pow(2))
        return distance < 2.5
    }

    //initial facing towards -z
    fun moveAnchors(event: MotionEvent, view: View): Pair<Float, Float> {

        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val pointX = (event.x * scaleFactorX) - center
        val pointZ = (event.y * scaleFactorY) - center

        val newX = -(pointX / (meter))
        val newZ = (pointZ / (meter))

        val x1 = (cos(rotation) * newX - sin(rotation) * newZ)
        val z1 = (sin(rotation) * newX + cos(rotation) * newZ)

        val x = x1 + camPosX
        val z = -z1 + camPosZ

//    1     = * 0.4
//    2.5   = * 1
//    5     = * 2
//        x = 0,4 * range
        return Pair(x, z)
    }
}