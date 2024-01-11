package com.example.foldAR.kotlin.helloar

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.foldAR.java.helpers.SnackbarHelper
import com.example.foldAR.java.helpers.TapHelper
import com.google.ar.core.Config

const val SCALE_FACTOR = 500
const val CIRCLE_RADIUS = 20f

/** Contains UI elements for Hello AR. */
class HelloArView(val activity: HelloArActivity) : DefaultLifecycleObserver {

    val calculatePosition = CalculatePosition()
    var anchorPos: FloatArray = FloatArray(3)

    val root = View.inflate(activity, R.layout.activity_main, null)
    val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
    val settingsButton = root.findViewById<ImageButton>(R.id.settings_button).apply {
        setOnClickListener { v ->
            PopupMenu(activity, v).apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.depth_settings -> launchDepthSettingsMenuDialog()
                        R.id.instant_placement_settings -> launchInstantPlacementSettingsMenuDialog()
                        else -> null
                    } != null
                }
                inflate(R.menu.settings_menu)
                show()
            }
        }
    }

    val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    private val paintX = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }


    //Todo fucking clean up code
    val moveView = root.findViewById<ImageView>(R.id.image_move_object).apply {
        setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN && activity.renderer.wrappedAnchors.isNotEmpty()) {
                val anchor = activity.renderer.wrappedAnchors[0].anchor.pose
                anchorPos[0] = anchor.tx()
                anchorPos[1] = anchor.ty()
                anchorPos[2] = anchor.tz()
            }
            if (event.action == MotionEvent.ACTION_MOVE) {
                val scaleFactorX = bitmap.width.toFloat() / view.width
                val scaleFactorY = bitmap.height.toFloat() / view.height

                val touchX = event.x * scaleFactorX
                val touchY = event.y * scaleFactorY

                calculatePosition.calculatePoints(touchX, touchY)

                val newX = anchorPos[0] + calculatePosition.returnValueZ() / SCALE_FACTOR
                val newZ = anchorPos[2] + calculatePosition.returnValueX() / SCALE_FACTOR
                activity.renderer.moveAnchor(newX, newZ, 0)

                Log.d("anchorPosInView", "View: $newX ------- $newZ ------- ${anchorPos[0]}")
                view.performClick()
                canvas.drawCircle(touchX, touchY, CIRCLE_RADIUS, paintX)
                setImageBitmap(bitmap)
            }
            if (event.action == MotionEvent.ACTION_UP) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                setImageBitmap(bitmap)
            }
            true
        }
    }


    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()
    val tapHelper = TapHelper(activity).also { surfaceView.setOnTouchListener(it) }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }

    /**
     * Shows a pop-up dialog on the first tap in HelloARRenderer, determining whether the user wants
     * to enable depth-based occlusion. The result of this dialog can be retrieved with
     * DepthSettings.useDepthForOcclusion().
     */
    fun showOcclusionDialogIfNeeded() {
        val session = session ?: return
        val isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
        if (!activity.depthSettings.shouldShowDepthEnableDialog() || !isDepthSupported) {
            return // Don't need to show dialog.
        }

        // Asks the user whether they want to use depth-based occlusion.
        AlertDialog.Builder(activity).setTitle(R.string.options_title_with_depth)
            .setMessage(R.string.depth_use_explanation)
            .setPositiveButton(R.string.button_text_enable_depth) { _, _ ->
                activity.depthSettings.setUseDepthForOcclusion(true)
            }.setNegativeButton(R.string.button_text_disable_depth) { _, _ ->
                activity.depthSettings.setUseDepthForOcclusion(false)
            }.show()
    }

    private fun launchInstantPlacementSettingsMenuDialog() {
        val resources = activity.resources
        val strings = resources.getStringArray(R.array.instant_placement_options_array)
        val checked = booleanArrayOf(activity.instantPlacementSettings.isInstantPlacementEnabled)
        AlertDialog.Builder(activity).setTitle(R.string.options_title_instant_placement)
            .setMultiChoiceItems(strings, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }.setPositiveButton(R.string.done) { _, _ ->
                val session = session ?: return@setPositiveButton
                activity.instantPlacementSettings.isInstantPlacementEnabled = checked[0]
                activity.configureSession(session)
            }.show()
    }

    /** Shows checkboxes to the user to facilitate toggling of depth-based effects. */
    private fun launchDepthSettingsMenuDialog() {
        val session = session ?: return

        // Shows the dialog to the user.
        val resources: Resources = activity.resources
        val checkboxes = booleanArrayOf(
            activity.depthSettings.useDepthForOcclusion(),
            activity.depthSettings.depthColorVisualizationEnabled()
        )
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            // With depth support, the user can select visualization options.
            val stringArray = resources.getStringArray(R.array.depth_options_array)
            AlertDialog.Builder(activity).setTitle(R.string.options_title_with_depth)
                .setMultiChoiceItems(stringArray, checkboxes) { _, which, isChecked ->
                    checkboxes[which] = isChecked
                }.setPositiveButton(R.string.done) { _, _ ->
                    activity.depthSettings.setUseDepthForOcclusion(checkboxes[0])
                    activity.depthSettings.setDepthColorVisualizationEnabled(checkboxes[1])
                }.show()
        } else {
            // Without depth support, no settings are available.
            AlertDialog.Builder(activity).setTitle(R.string.options_title_without_depth)
                .setPositiveButton(R.string.done) { _, _ -> /* No settings to apply. */ }.show()
        }
    }
}
