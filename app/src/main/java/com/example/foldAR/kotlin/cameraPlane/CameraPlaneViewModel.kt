package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

//range +-2.5m on x/z axis
const val range = 2.5f

//Todo make cleaner
class CameraPlaneViewModel : ViewModel() {
    private var setRange = 2.5f //change to get other scale on map

    private val bitmap: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var scaleX: Float? = null
    private var scaleZ: Float? = null


    var canvasPosX = 250f
    var canVasPosY = 0f
    var canvasPosZ = 250f

    fun setData(view: View) {
        scaleX = bitmap.width.toFloat() / view.width
        scaleZ = bitmap.height.toFloat() / view.height
        paint.color = Color.RED
        paint.strokeWidth = 2f
    }

    fun mapAnchors(
        camera: Camera,
        wrappedAnchors: MutableList<WrappedAnchor>,
        view: ImageView,
    ): Bitmap {
        val camPosX = camera.pose.translation[0]
        val camPosZ = camera.pose.translation[2]

        bitmap.eraseColor(Color.TRANSPARENT) //Todo mby use BitmapFactory
        for (i in wrappedAnchors) {
            //x,y,z
            val poseX = i.anchor.pose.tx()
            val poseZ = i.anchor.pose.tz()

            if (!(isInRange(poseX, camPosX) || isInRange(poseZ, camPosZ)))
                drawPoint(camPosX, camPosZ, poseX, poseZ, camera)
        }
        return bitmap
    }

    private fun drawPoint(
        camPosX: Float,
        camPosZ: Float,
        poseX: Float,
        poseZ: Float,
        camera: Camera,
    ) {

        val center = 250f
        val canvas = Canvas(bitmap)
        canvas.drawCircle(center, center, 10f, paint)

        val newX: Float = abs(poseX - camPosX)
        val newZ: Float = abs(poseZ - camPosZ)

        if (camPosX <= poseX)
            canvasPosX += (newX * (setRange / range) * 100 * camera.pose.xAxis[0])
        else
            canvasPosX -= (newX * (setRange / range) * 100 * camera.pose.xAxis[0])

        if (camPosZ <= poseZ)
            canvasPosZ += (newZ * (setRange / range) * 100 * camera.pose.zAxis[2])
        else
            canvasPosZ -= (newZ * (setRange / range) * 100 * camera.pose.zAxis[2])

        calculatePoints(camera)
        canvas.drawCircle(canvasPosX, canvasPosZ, 20f, paint)
    }

    private fun calculatePoints(camera: Camera){
                canvasPosX += (canvasPosZ * cos(camera.pose.xAxis[0]) - canVasPosY * sin(camera.pose.xAxis[0]))
                canvasPosZ += (canvasPosZ * sin(camera.pose.xAxis[0]) + canVasPosY * cos(camera.pose.xAxis[0]))
    }


    private fun isInRange(pose: Float, camPos: Float): Boolean {
        return abs(pose - camPos) > range
    }
}