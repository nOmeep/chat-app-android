package com.example.messenger.helpfulfiles

import android.view.MotionEvent
import android.view.View

fun onTouchAnimated(vararg buttons: View) {
    for (btn in buttons) {
        btn.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> btn.animate().scaleX(1.03f).scaleY(1.03f).duration = 50
                MotionEvent.ACTION_UP -> btn.animate().scaleX(1f).scaleY(1f).duration = 50
            }
            v?.onTouchEvent(event) ?: true
        }
    }
}