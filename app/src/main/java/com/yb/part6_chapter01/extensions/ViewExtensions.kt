package com.yb.part6_chapter01.extensions

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.toVisible() {
    this.isVisible = true
}

fun View.toGone() {
    this.isGone = true
}