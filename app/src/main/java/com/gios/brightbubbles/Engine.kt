package com.gios.brightbubbles

/**
 * The Rust engine (rustpush), loaded from `libbrightbubbles.so`.
 *
 * Every call is JSON in, JSON out: the Kotlin side never holds a Rust object, so a crash on either
 * side of the boundary cannot leave the other holding a dangling pointer.
 */
object Engine {
    val loaded: Result<Unit> = runCatching { System.loadLibrary("brightbubbles") }

    /** `{"engine": "...", "rustpush": "<rev>", ...}`. */
    external fun version(): String
}
