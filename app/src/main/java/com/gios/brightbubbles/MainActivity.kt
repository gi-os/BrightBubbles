package com.gios.brightbubbles

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

/**
 * Milestone 1: shows that the engine loaded and which rustpush it was built from. The setup
 * screens (Apple ID, validation source) replace this in milestone 2.
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = Engine.loaded.fold(
            onSuccess = { runCatching { Engine.version() }.getOrElse { "Engine loaded, call failed: $it" } },
            onFailure = { "Engine didn’t load: $it" },
        )
        setContentView(
            TextView(this).apply {
                setText("BrightBubbles\n\n$text")
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.BLACK)
                gravity = Gravity.CENTER
                textSize = 16f
                setPadding(48, 48, 48, 48)
            },
        )
    }
}
