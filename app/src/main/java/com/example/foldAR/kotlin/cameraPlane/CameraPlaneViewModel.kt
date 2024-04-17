package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class CameraPlaneViewModel : ViewModel() {

    companion object {
        private const val range = 2.5f
        private val Tag = "ViewModelTAG"
    }

    private lateinit var camera: Camera
    private var rotation: Float = 0f

    private val bitmap: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 2f
    }

    private var scaleX: Float? = null
    private var scaleZ: Float? = null

    fun setData(view: View) {
        scaleX = bitmap.width.toFloat() / view.width
        scaleZ = bitmap.height.toFloat() / view.height
    }

    fun mapAnchors(
        camera: Camera,
        wrappedAnchors: MutableList<WrappedAnchor>,
    ): Bitmap {
        this.camera = camera
        bitmap.eraseColor(Color.TRANSPARENT) //Todo mby use BitmapFactory

        val camPose = camera.pose.translation
        val (camPosX, camPosZ) = arrayOf(camPose[0], camPose[2])

        // Ignore the roll and pitch (tilt), and only use the yaw for the 2D rotation
        val rotationPlane = quaternionToYaw(camera.pose.rotationQuaternion)

        wrappedAnchors.forEach {
            val anchorPose = it.anchor.pose
            val (anchorPoseX, anchorPoseZ) = arrayOf(anchorPose.tx(), anchorPose.tz())

            if (isInRange(anchorPoseX, anchorPoseZ, camPosX, camPosZ)) {
                drawPoint(camPosX, camPosZ, anchorPoseX, anchorPoseZ, rotationPlane)
            }
        }

        return bitmap
    }


    private fun drawPoint(
        camPosX: Float,
        camPosZ: Float,
        poseX: Float,
        poseZ: Float,
        rotation1: Float
    ) {
        var rotation = rotation1

        if (rotation < 0)
            rotation += 360

        rotation = (rotation1 / 180 * PI).toFloat()

        this.rotation = rotation

        Log.d(Tag, "camera: ${this.rotation}")

        val canvas = Canvas(bitmap)
        val newX = (poseX - camPosX) * 100
        val newZ = (poseZ - camPosZ) * 100

        val newXRotatedX = cos(rotation) * newX - sin(rotation) * newZ
        val newRotatedZ = sin(rotation) * newX + cos(rotation) * newZ

        canvas.drawCircle(-newXRotatedX + 250, -newRotatedZ + 250, 5f, paint)
    }

    private fun quaternionToYaw(quaternion: FloatArray): Float {
        val (w, x, y, z) = quaternion
        val sinyCosp = 2 * (w * z + x * y)
        val cosyCosp = 1 - 2 * (y * y + z * z)
        val yaw = atan2(sinyCosp, cosyCosp)
        return Math.toDegrees(yaw.toDouble()).toFloat()
    }


    private fun isInRange(poseX: Float, poseZ: Float, camPosX: Float, camPosZ: Float): Boolean {
        val distance = kotlin.math.sqrt((poseX - camPosX).pow(2) + (poseZ - camPosZ).pow(2))
        return distance < range
    }

    fun moveAnchors(event: MotionEvent, view: View): Array<Float> {

        Log.d(Tag, "move: ${this.rotation}")

        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val pointX = (event.x * scaleFactorX) - 250
        val pointZ = (event.y * scaleFactorY) - 250

        val (camPosX, camPosZ) = arrayOf(camera.pose.translation[0], camera.pose.translation[2])

        val X1 = (cos(rotation) * pointX + sin(rotation) * pointZ) / 100
        val Z1 = (-sin(rotation) * pointX + cos(rotation) * pointZ) / 100

        val newX = camPosX + X1
        val newZ = camPosZ + Z1

        return arrayOf(-newX, -newZ)
    }
}