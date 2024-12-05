package com.example.foldAR.kotlin.cameraPlane

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.Constants
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Camera
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/** Initially facing towards negative z. Thus swapping left/right and front/behind.
 * Because a bitmaps origin is in the upper left, it need to be centered and the x-axis needs to be swapped.
 * No need to swap the z-axis, because towards top is already negative.
 * Because the bitmap has a size of 500x500, the points drawn and moved need to be normalized to meters.
 * */
class CameraPlaneViewModel : ViewModel() {

    //Only formatting because I need to call Constants less
    companion object {
        private const val bitmapSize = Constants.bitmapSize
        private const val bitmapSizeFloat = Constants.bitmapSize.toFloat()
        private const val midPoint = bitmapSizeFloat / 2
        private const val radius = 5f

        private val  paintObjects = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            strokeWidth = 2f
        }

        private val paintObjectChosen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            strokeWidth = 2f
        }

        private val paintAxis = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            strokeWidth = 2f
        }

        private val paintGrid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            strokeWidth = 1f
        }

    }

    private var _range = 1f

    /**Times 0.4 because bitmap sides are 250cm
     * Without it range of 1m would be 2.5m at each side scaling it out of proportion
     * I.e. 1m = 250/2.5
     *      2m = 250 / 2.5 * 2
     *    ..5m = 250 / 2.5 * 5
     *    Using multiplication because it needs less computing power than division
     *    */
    private val range get() = _range * 0.4f

    private var _currentPosition = 0
    private val currentPosition get() = _currentPosition

    private var camPosX = 0f
    private var camPosZ = 0f
    private var rotation: Float = 0f
    private var center = bitmapSize / 2

    private val bitmap: Bitmap =
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)

    private val bitmapCoordinateSystem: Bitmap =
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)

    fun setRange(range: Float) {
        if (range in Constants.sliderMin..Constants.sliderMax)
            _range = range
    }

    fun setCurrentPosition(position: Int) {
        if (position in 0..Constants.objectsMaxSize + 1)
            _currentPosition = position
    }

    //map the available objects onto the bitmap
    suspend fun mapAnchors(
        camera: Camera,
        wrappedAnchors: MutableList<WrappedAnchor>,
        refreshAngle: Float,
    ): Bitmap {

        this.rotation = refreshAngle
        this.camPosX = camera.pose.translation[0]
        this.camPosZ = camera.pose.translation[2]

        bitmap.eraseColor(Color.TRANSPARENT)

        val mutex = Mutex()

        val wrappedAnchorsCopy = wrappedAnchors.toList()

        mutex.withLock {
            wrappedAnchorsCopy.withIndex()
                .forEach { //Todo crashes when adding new object while updating it. Mby fixed. Look into it with additional testing!! Mby the RecyclerView isn´t updated properly
                    val anchorPose = it.value.anchor.pose
                    val (anchorPoseX, anchorPoseZ) = arrayOf(anchorPose.tx(), anchorPose.tz())

                    if (isInRange(anchorPoseX, anchorPoseZ, camPosX, camPosZ)) {
                        drawPoint(anchorPoseX, anchorPoseZ, it.index)
                    }
                }

            return combineBitmaps()
        }
    }

    private fun combineBitmaps() : Bitmap{
        val combinedBitmap = Bitmap.createBitmap(Constants.bitmapSize, Constants.bitmapSize, bitmap.config)
        val canvas = Canvas(combinedBitmap)
        canvas.drawBitmap(bitmapCoordinateSystem, 0f,0f, null)
        canvas.drawBitmap(bitmap, 0f,0f,null)
        return combinedBitmap
    }

    /** Draw the point by rotating it by the phones yaw and adding the camera coordinates*/
    private fun drawPoint(
        poseX: Float,
        poseZ: Float,
        index: Int
    ) {

        val canvas = Canvas(bitmap)
        val newX = (poseX - camPosX) * (Constants.meterToCm)
        val newZ = (poseZ - camPosZ) * (Constants.meterToCm)

        val newRotatedX = (cos(rotation) * newX - sin(rotation) * newZ) / range
        val newRotatedZ = (sin(rotation) * newX + cos(rotation) * newZ) / range

        canvas.drawCircle(
            (-newRotatedX + center),
            (-newRotatedZ + center),
            radius,
            if (index == currentPosition) paintObjectChosen else paintObjects
        )
    }

    //checks if the point is in a circle with the radius of the designated range
    private fun isInRange(poseX: Float, poseZ: Float, camPosX: Float, camPosZ: Float): Boolean {
        val distance = kotlin.math.sqrt((poseX - camPosX).pow(2) + (poseZ - camPosZ).pow(2))
        return distance * 0.4 < range
    }


    /**Moving the anchor to the point clicked by rotating the point by the phones yaw and adding the cameras
     * position to it.
     * */
    fun moveAnchors(event: MotionEvent, view: View): Pair<Float, Float> {

        val scaleFactorX = bitmap.width.toFloat() / view.width
        val scaleFactorY = bitmap.height.toFloat() / view.height

        val pointX = (event.x * scaleFactorX) - center
        val pointZ = (event.y * scaleFactorY) - center

        val newX = -(pointX / (Constants.meterToCm))
        val newZ = (pointZ / (Constants.meterToCm))

        val x1 = (cos(rotation) * newX - sin(rotation) * newZ) * range
        val z1 = (sin(rotation) * newX + cos(rotation) * newZ) * range

        val x = x1 + camPosX
        val z = -z1 + camPosZ

        return Pair(x, z)
    }

    fun drawCoordinateSystem(): Bitmap {
        bitmapCoordinateSystem.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bitmapCoordinateSystem)


        drawAxis(canvas)
        drawGrid(canvas)

        return combineBitmaps()
    }

    private fun drawAxis(canvas: Canvas) {
        val endOffset = 20f

        // Draw horizontal and vertical lines
        drawLine(0f, midPoint, bitmapSizeFloat, midPoint, paintAxis, canvas)
        drawLine(midPoint, 0f, midPoint, bitmapSizeFloat, paintAxis, canvas)

        // Draw arrow heads for x-axis
        drawLine(
            bitmapSizeFloat, midPoint, bitmapSizeFloat - endOffset, midPoint - endOffset, paintAxis, canvas
        )
        drawLine(
            bitmapSizeFloat, midPoint, bitmapSizeFloat - endOffset, midPoint + endOffset, paintAxis, canvas
        )

        // Draw arrow heads for y-axis
        drawLine(
            midPoint, 0f, midPoint - endOffset, endOffset, paintAxis, canvas
        )
        drawLine(
            midPoint, 0f, midPoint + endOffset, endOffset, paintAxis, canvas
        )
    }

    private fun drawGrid(canvas: Canvas) {

        val distance = (bitmapSize / 2) / (_range + 1)

        var i = 1f
        do {
            drawLine(
                midPoint - (distance * i), 0f, midPoint - (distance * i), bitmapSizeFloat, paintGrid, canvas
            )
            drawLine(
                midPoint + (distance * i), 0f, midPoint + (distance * i), bitmapSizeFloat, paintGrid, canvas
            )
            drawLine(
                0f, midPoint - (distance * i), bitmapSizeFloat, midPoint - (distance * i), paintGrid, canvas
            )
            drawLine(
                0f, midPoint + (distance * i), bitmapSizeFloat, midPoint + (distance * i), paintGrid, canvas
            )

            i++
        } while (i < _range + 1)
    }

    private fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint, canvas: Canvas) =
        canvas.drawLine(startX, startY, endX, endY, paint)
}