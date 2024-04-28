package com.example.foldAR.kotlin.helperClasses

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.foldAR.kotlin.helloar.R

@Suppress("SameParameterValue")
class SwipeBackgroundHelper {

    companion object {

        private const val OFFSET_PX = 20

        private var usedDisplay: Double? = null

        private lateinit var colorGradingDelete: ColorGradingDelete

        @JvmStatic
        fun paintDrawCommandToStart(
            canvas: Canvas,
            viewItem: View,
            @DrawableRes iconResId: Int,
            dX: Float,
            colorGradingDelete: ColorGradingDelete,
        ) {
            Companion.colorGradingDelete = colorGradingDelete
            usedDisplay = colorGradingDelete.usedDisplay

            val drawCommand = createDrawCommand(viewItem, dX, iconResId)
            paintDrawCommand(drawCommand, canvas, dX, viewItem)
        }

        private fun createDrawCommand(viewItem: View, dX: Float, iconResId: Int): DrawCommand {
            val context = viewItem.context
            var icon = ContextCompat.getDrawable(context, iconResId)

            icon = DrawableCompat.wrap(icon!!).mutate()
            icon.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.white),
                PorterDuff.Mode.SRC_IN
            )
            val backgroundColor = getBackgroundColor(dX)
            return DrawCommand(icon, backgroundColor)
        }

        private fun getBackgroundColor(
            dX: Float,
        ): Int {
            //return either final color or color grading
            return when (dX >= usedDisplay!!) {
                true -> colorGradingDelete.calculateColor(usedDisplay!!.toFloat())
                false -> {
                    colorGradingDelete.calculateColor(dX)
                }
            }
        }

        private fun paintDrawCommand(
            drawCommand: DrawCommand,
            canvas: Canvas,
            dX: Float,
            viewItem: View,
        ) {
            drawBackground(canvas, viewItem, dX, drawCommand.backgroundColor)
            drawIcon(canvas, viewItem, dX, drawCommand.icon)
        }

        private fun drawIcon(canvas: Canvas, viewItem: View, dX: Float, icon: Drawable) {
            val topMargin = calculateTopMargin(icon, viewItem)
            icon.bounds =
                getStartContainerRectangle(viewItem, icon.intrinsicWidth, topMargin, OFFSET_PX, dX)
            icon.draw(canvas)
        }

        private fun getStartContainerRectangle(
            viewItem: View, iconWidth: Int, topMargin: Int, sideOffset: Int,
            dx: Float,
        ): Rect {
            val leftBound = viewItem.left - iconWidth + dx.toInt() - sideOffset
            val rightBound = viewItem.left + dx.toInt() - sideOffset
            val topBound = viewItem.top + topMargin
            val bottomBound = viewItem.bottom - topMargin

            return Rect(leftBound, topBound, rightBound, bottomBound)
        }

        private fun calculateTopMargin(icon: Drawable, viewItem: View): Int {
            return (viewItem.height - icon.intrinsicHeight) / 4
        }

        private fun drawBackground(canvas: Canvas, viewItem: View, dX: Float, color: Int) {
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.color = color
            val backgroundRectangle = getBackGroundRectangle(viewItem, dX)
            canvas.drawRect(backgroundRectangle, backgroundPaint)
        }

        //For Background
        private fun getBackGroundRectangle(viewItem: View, dX: Float): RectF {
            return RectF(
                viewItem.left.toFloat(), viewItem.top.toFloat(), viewItem.left.toFloat() + dX,
                viewItem.bottom.toFloat()
            )
        }
    }

    private class DrawCommand(
        val icon: Drawable,
        val backgroundColor: Int,
    )

}