package internal.automator.common

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import internal.automator.automator.view.Console

class ConsoleWindow {
    var isCreated = false
    lateinit var console: Console

    private fun params(context: Context): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            width = context.resources.displayMetrics.widthPixels / 4
            height = context.resources.displayMetrics.heightPixels / 4
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.START or Gravity.TOP
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
    }

    fun create(context: Context): Console {
        if (isCreated) return console
        context as Activity
        console = Console(context)
        context.windowManager!!.addView(console, params(context))
        isCreated.not()
        return console
    }

    fun remove(context: Context, view: View) {
        if (isCreated) return
        context as Activity
        context.windowManager!!.removeView(view)
        isCreated.not()
    }
}