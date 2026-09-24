//! BrightBubbles: rustpush on the phone, reached from Kotlin over JNI.
//!
//! Milestone 1 is only this: the engine compiles for arm64 Android, loads, and answers. The
//! Apple sign-in, registration and the message loop come next, one JNI call at a time, each one
//! a small JSON-in / JSON-out function so the Kotlin side never holds a Rust object.

use jni::objects::JClass;
use jni::sys::jstring;
use jni::JNIEnv;

/// The engine's own version, and the rustpush revision it was built from.
#[no_mangle]
pub extern "system" fn Java_com_gios_brightbubbles_Engine_version(env: JNIEnv, _class: JClass) -> jstring {
    init_logging();
    // Touch real rustpush types so the build proves rustpush compiles and links for Android,
    // not only that a string can cross JNI.
    let body = serde_json::json!({
        "engine": env!("CARGO_PKG_VERSION"),
        "rustpush": option_env!("RUSTPUSH_REV").unwrap_or("unknown"),
        "apsStateBytes": std::mem::size_of::<rustpush::APSState>(),
        "validation": ["mac-hardware", "iphone-relay"],
    })
    .to_string();
    env.new_string(body).map(|s| s.into_raw()).unwrap_or(std::ptr::null_mut())
}

fn init_logging() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_max_level(log::LevelFilter::Info)
            .with_tag("BrightBubbles"),
    );
}
