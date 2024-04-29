package com.example.foldAR.kotlin.dialog

import androidx.lifecycle.ViewModel
import com.example.foldAR.kotlin.renderer.WrappedAnchor
import com.google.ar.core.Anchor

class DialogOptionsViewModel : ViewModel() {

    fun getList(wrappedAnchors: List<WrappedAnchor>): List<Anchor> =
        wrappedAnchors.map { it.anchor }
}