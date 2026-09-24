#!/usr/bin/env bash
# Checks out rustpush and its submodules over HTTPS and applies BrightBubbles' changes to it.
# Idempotent: run it before every engine build.
set -euo pipefail
cd "$(dirname "$0")/.."
# rustpush lists its own submodules as SSH URLs; CI has no SSH key.
git config --global url."https://github.com/".insteadOf "git@github.com:"
git submodule update --init --recursive --depth 1
python3 scripts/patch_rustpush.py engine/rustpush
