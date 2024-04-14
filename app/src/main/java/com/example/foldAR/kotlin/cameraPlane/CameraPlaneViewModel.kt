package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlin.math.abs

//range +-2.5m on x/z axis
const val range = 2.5f

//Todo make cleaner
class CameraPlaneViewModel : ViewModel() {
    private var setRange = 2.5f //change to get other scale on map

    private val bitmap: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var scaleX: Float? = null
    private var scaleZ: Float? = null


    private var canvasPosX = 0f
    private var canVasPosY = 0f
    private var canvasPosZ = 0f

    fun setData(view: View) {
        scaleX = bitmap.width.toFloat() / view.width
        scaleZ = bitmap.height.toFloat() / view.height
        paint.color = Color.RED
        paint.strokeWidth = 2f
    }

    fun mapAnchors(
        camera: Camera,
        wrappedAnchors: MutableList<WrappedAnchor>,
    ): Bitmap {

        //Camera position in flat space
        val camPosX = camera.pose.translation[0]
        val camPosZ = camera.pose.translation[2]

        bitmap.eraseColor(Color.TRANSPARENT) //Todo mby use BitmapFactory
        for (i in wrappedAnchors) {
            //Object pose in flat space
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
        val canvas = Canvas(bitmap)
        val canvasPosX: Float = (poseX - camPosX) * 100 + 250
        val canvasPosZ = (poseZ - camPosZ) * 100 + 250

        canvas.drawCircle(canvasPosX, canvasPosZ, 20f, paint)
    }

    private fun isInRange(pose: Float, camPos: Float): Boolean {
        return abs(pose - camPos) > range
    }
}