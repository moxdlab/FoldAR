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

class CameraPlaneViewModel : ViewModel() {
    private val bitmap: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var scaleX: Float? = null
    private var scaleZ: Float? = null


    fun setData(view: View) {
        scaleX = bitmap.width.toFloat() / view.width
        scaleZ = bitmap.height.toFloat() / view.height
        paint.color = Color.RED
        paint.strokeWidth = 2f
    }

    //scale +-2m on x/z axis
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
                drawPoint(camPosX, camPosZ, poseX, poseZ)
        }
        return bitmap
    }

    private fun drawPoint(
        camPosX: Float,
        camPosZ: Float,
        poseX: Float,
        poseZ: Float,
    ) {

        val center = 250f
        val canvas = Canvas(bitmap)
        canvas.drawCircle(center, center, 10f, paint)

        val newX: Float = abs(poseX - camPosX)
        val newZ: Float = abs(poseZ - camPosZ)

        var canvasPosX = 250f
        var canvasPosZ = 250f

        if (camPosX < poseX)
            canvasPosX -= (newX * 100)
        else
            canvasPosX += (newX * 100)

        if (camPosZ < poseZ)
            canvasPosZ -= (newZ * 100)
        else
            canvasPosZ += (newZ * 100)

        canvas.drawCircle(canvasPosX, canvasPosZ, 20f, paint)
    }

    private fun isInRange(pose: Float, camPos: Float): Boolean {
        return abs(pose - camPos) > 2.5
    }
}