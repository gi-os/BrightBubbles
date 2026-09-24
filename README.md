# BrightBubbles

iMessage on the Light Phone III with no Mac left running. BrightBubbles runs
[rustpush](https://github.com/OpenBubbles/rustpush), OpenBubbles' iMessage engine, on the phone. It
gives [BrightChat](https://github.com/gi-os/BrightChat) the same BlueBubbles API a Mac server
does, on `127.0.0.1`. BrightChat connects to it the way it connects to a Mac today.

**Status: milestone 1.** The engine builds for arm64 Android and loads. It does not sign in or
send messages yet.

## Why a separate app

rustpush is licensed under the SSPL. BrightChat is MIT. If rustpush ran inside BrightChat's own
process, the two would be one program, and BrightChat would have to become SSPL too. That is true
even if BrightChat downloaded the engine after install. So the engine is its own app, in its own
process, with its own license. BrightChat talks to it over a local socket. For you it is still one
tap: BrightChat offers **Install iMessage engine** and installs this APK.

## How it proves it is an Apple device

To register for iMessage, Apple asks for *validation data*, a short signed proof that the request
comes from real Apple hardware. BrightBubbles supports two sources:

| Source | What you need | What you get |
| --- | --- | --- |
| **Mac, once** | Run a hardware-info tool once on a Mac you own | Your Apple ID emails on iMessage. The Mac can then be switched off. |
| **iPhone relay** | An old iPhone with a validation relay app, always online | Your phone number on iMessage |

The Mac route needs code that produces validation data on the phone. rustpush ships only a stub
for it (`open-absinthe`), so BrightBubbles adds its own. It needs Apple's own `IMDAppleServices`
binary, which you copy from your Mac during setup. This repository does not include it.

## Plan

1. **Engine builds** (this milestone). rustpush, patched, compiled with `cargo-ndk`. A JNI library
   packaged in a signed APK.
2. **Sign in.** Apple ID and two-factor code, anisette from an anisette-v3 server (self-hostable),
   APS push connection, state saved on the phone.
3. **iPhone relay.** Register with validation data from a relay (rustpush `RelayConfig`), then send
   and receive a first message.
4. **Mac route.** Hardware-info import and on-phone validation data.
5. **BlueBubbles API.** A local REST and Socket.IO server that covers what BrightChat uses: chats,
   messages, send, attachments, tapbacks, typing and read state.
6. **BrightChat.** An "Install iMessage engine" choice on the setup screen, and the server URL
   filled in.
7. **Background.** Foreground service, APS keep-alive, and registration renewal while the phone sleeps.

## Build

```sh
./scripts/prepare-rustpush.sh        # submodules over HTTPS, then patch_rustpush.py
cd engine && cargo ndk -t arm64-v8a --platform 34 -o ../app/src/main/jniLibs build --release
cd .. && ./gradlew :app:assembleRelease
```

CI does the same on every push and publishes a nightly APK.

## Changes to rustpush

`scripts/patch_rustpush.py` applies them to the pinned checkout. Each change must apply exactly
once, or the build stops:

- Activation signs with the FairPlay certificate in `certs/legacy-fairplay`. The certificates the
  source lists in `certs/fairplay/` are not in the upstream repository.

## License

[SSPL-1.0](LICENSE), the same as rustpush. Credit to [OpenBubbles](https://github.com/OpenBubbles)
and the pypush project for the iMessage protocol work.

iMessage, Apple, Mac and iPhone are trademarks of Apple Inc. This project is not affiliated with
Apple.
