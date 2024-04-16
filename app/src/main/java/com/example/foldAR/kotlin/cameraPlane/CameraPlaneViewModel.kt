package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlin.math.atan2
import kotlin.math.pow

//Todo make cleaner
class CameraPlaneViewModel : ViewModel() {

    companion object {
        private const val range = 2.5f
        private val Tag = "ViewModelTAG"
    }

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
        bitmap.eraseColor(Color.TRANSPARENT) //Todo mby use BitmapFactory

        val camPose = camera.pose.translation
        val (camPosX, camPosZ) = arrayOf(camPose[0], camPose[2])

        // Ignore the roll and pitch (tilt), and only use the yaw for the 2D rotation
        val rotationPlane = quaternionToYaw(camera.pose.rotationQuaternion)

        wrappedAnchors.forEach {
            val anchorPose = it.anchor.pose
            val (anchorPoseX, anchorPoseZ) = arrayOf(anchorPose.tx(), anchorPose.tz())

            if (isInRange(anchorPoseX, anchorPoseZ, camPosX, camPosZ)) {
                drawPoint(camPosX, camPosZ, anchorPoseX, anchorPoseZ, camera, rotationPlane)
            }
        }

        return bitmap
    }


    private fun drawPoint(
        camPosX: Float,
        camPosZ: Float,
        poseX: Float,
        poseZ: Float,
        camera: Camera,
        rotation: Float
    ) {
        Log.d(Tag, rotation.toString())

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
}