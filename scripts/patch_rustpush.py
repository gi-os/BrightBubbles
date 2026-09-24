"""BrightBubbles' changes to the pinned rustpush checkout, applied in place.

1. Activation signs with the FairPlay certificate rustpush ships in `certs/legacy-fairplay`.
   Its source lists ten per-device certificates under `certs/fairplay/`, a directory that is
   git-ignored upstream and so never in a checkout; the build cannot compile without this.

Each change checks that it applies exactly once, so an upstream move fails loudly here instead
of producing an engine that quietly does something else.
"""
import pathlib
import re
import sys

root = pathlib.Path(sys.argv[1])

act = root / "src" / "activation.rs"
s = act.read_text()
if "legacy-fairplay" not in s:
    new, n = re.subn(
        r"const FAIRPLAY_KEYS: &\[\(&'static \[u8\], &'static \[u8\]\)\] = &\[.*?\];",
        "const FAIRPLAY_KEYS: &[(&'static [u8], &'static [u8])] = &[\n"
        "    (include_bytes!(\"../certs/legacy-fairplay/fairplay.crt\"), include_bytes!(\"../certs/legacy-fairplay/fairplay.pem\")),\n"
        "];",
        s,
        count=1,
        flags=re.S,
    )
    if n != 1:
        sys.exit("activation.rs: FAIRPLAY_KEYS block not found")
    act.write_text(new)
    print("patched activation.rs: legacy FairPlay certificate")
else:
    print("activation.rs already patched")
