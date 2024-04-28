package com.example.foldAR.kotlin.helperClasses

import android.graphics.Color
import kotlin.math.pow

@Suppress("SameParameterValue")
class ColorGradingDelete(displayWidth: Int) {
    //Size is displayWidth in dX, percent is percent of display until end Color is reached,
    //startingValue first color and endValue is color after operation

    //defines how flat the graph is and through which colors the background cycles
    private val powRed: Double = 2.8
    private val powGreen: Double = 2.8
    private val powBlue: Double = 2.8

    //variables for color grading
    private val startRed: Int = 36
    private val startGreen: Int = 35
    private val startBlue: Int = 35

    //end values of colors
    private val endRed: Int = 200
    private val endGreen: Int = 0
    private val endBlue: Int = 0

    //percent of display before reaching final color
    private val percent: Double = 32.4074074

    private val _usedDisplay: Double = (displayWidth * percent) / 100
    val usedDisplay get() = _usedDisplay

    //color grading multipliers. Used to calculate correct end values
    private var colorMultiplierRed: Double =
        displaySizeForColorGrading(powRed, startRed, endRed)
    private var colorMultiplierGreen: Double =
        displaySizeForColorGrading(powGreen, startGreen, endGreen)
    private var colorMultiplierBlue: Double =
        displaySizeForColorGrading(powBlue, startBlue, endBlue)

    private fun displaySizeForColorGrading(
        pow: Double,
        startingValue: Int,
        endValue: Int,
    ): Double = (endValue - startingValue) / (usedDisplay.pow(pow))

    fun calculateColor(point: Float): Int = Color.rgb(
        (point.toDouble().pow(powRed) * colorMultiplierRed + startRed).toInt(),
        (point.toDouble().pow(powGreen) * colorMultiplierGreen + startGreen).toInt(),
        (point.toDouble().pow(powBlue) * colorMultiplierBlue + startBlue).toInt()
    )
}