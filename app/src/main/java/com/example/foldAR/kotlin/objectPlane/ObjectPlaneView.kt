package com.example.foldAR.kotlin.objectPlane

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
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
import com.example.foldAR.kotlin.anchorManipulation.ChangeAnchor
import com.example.foldAR.kotlin.helloar.R
import com.google.ar.core.Config


const val SCALE_FACTOR_SLOW = 5000
const val SCALE_FACTOR_MEDIUM = 2000
const val SCALE_FACTOR_FAST = 500

/** Contains UI elements for Hello AR. */
class ObjectPlaneView(val activity: ObjectPlaneActivity) : DefaultLifecycleObserver {

    private val changeAnchor = ChangeAnchor()
    private var anchorPos: FloatArray = FloatArray(3)

    val root: View = View.inflate(activity, R.layout.activity_object_plane, null)
    val surfaceView: GLSurfaceView = root.findViewById(R.id.surfaceview)

    //for viewing the points on the map
    private val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    //change moving speed of the object
    val settingsButton = root.findViewById<ImageButton>(R.id.settings_button).apply {
        setOnClickListener { v ->
            PopupMenu(activity, v).apply {
                setOnMenuItemClickListener { item -> updateScaleFactor(item.itemId) }
                inflate(R.menu.settings_menu)
                show()
            }
        }
    }!!

    private fun updateScaleFactor(itemId: Int): Boolean {
        return when (itemId) {
            R.id.slow -> {
                changeAnchor.setScaleFactor(SCALE_FACTOR_SLOW)
                true
            }

            R.id.medium -> {
                changeAnchor.setScaleFactor(SCALE_FACTOR_MEDIUM)
                true
            }

            R.id.fast -> {
                changeAnchor.setScaleFactor(SCALE_FACTOR_FAST)
                true
            }

            else -> false
        }
    }

    val moveView = { id: Int, action: (Float, Float, Int) -> Unit ->
        root.findViewById<ImageView>(id).apply {
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN && activity.renderer.wrappedAnchors.isNotEmpty()) {
                    anchorPos = activity.renderer.getAnchorPosition(0)!!
                }
                if (event.action == MotionEvent.ACTION_MOVE && activity.renderer.wrappedAnchors.isNotEmpty()) {
                    changeAnchor.getNewPosition(event, view, bitmap, anchorPos)

                    action(changeAnchor.newX, changeAnchor.newZ, 0)

                    view.performClick()
                }
                true
            }
        }!!
    }

    val moveViewPlane = moveView(R.id.image_move_object_plane, activity.renderer::moveAnchorPlane)
    val moveViewHeight = moveView(R.id.image_move_object_height) { _, _, position ->
        activity.renderer.moveAnchorHeight(changeAnchor.newY, position)
    }

    private val session
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
}