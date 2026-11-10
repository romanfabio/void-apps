---
name: release-app
description: Produce a signed release APK of a Void app, tag it, and publish a GitHub Release, following docs/CICD.md §4. Use when the owner asks to release, ship, publish, or build a signed APK of an app.
---

# release-app

Signing happens only on the owner's machine. Pushing tags and creating GitHub Releases
are outward-facing: **confirm each with the owner before running it.**

## 1. Preconditions — stop and report if any fails

1. Working tree clean, on `main`, up to date with `origin/main`.
2. `keystores/<app>.jks` exists and `keystores/keystore.properties` has
   `<app>.storePassword` (and `<app>.keyPassword` if different).
3. ⚠️ If `docs/SECURITY.md` still marks the keystore backup location as
   "DECISION NEEDED", warn the owner that losing the key makes updates impossible, and
   continue only after they acknowledge.
4. `gh` CLI is authenticated (`gh auth status`).

## 2. Decide the version (ask the owner)

Current version: `apps/<app>/version.properties`. SemVer:
- PATCH: fixes only. MINOR: new features, backwards-compatible data. MAJOR: breaking
  changes (data loss, removed features).
- First release beyond the owner's own device is `1.0.0` (docs/NAMING.md §8).

## 3. Verify

```bash
./gradlew :apps:<app>:check
```

Then the full real-device procedure (docs/TESTING.md §7, `verify` skill step 3b) on
**every real device attached** — emulators never count. Cover the app's minSdk and
targetSdk where such devices exist (docs/ANDROID_SUPPORT.md §5); state any uncovered API
levels in the release notes. All must pass. Report model, Android version and API level of
each device.

After step 5, also install the **signed release APK** on a real device
(`adb install -r …`) and repeat the launch + About & licenses check: R8 shrinking only
affects release builds.

## 4. Bump and commit

1. Set `version=X.Y.Z` in `apps/<app>/version.properties`.
2. Add a row to "Release history" in `apps/<app>/README.md` (date `YYYY-MM-DD`, notes).
3. Commit: `chore(<app>): release vX.Y.Z`.

## 5. Build and sign

```bash
./gradlew :apps:<app>:exportReleaseApk
```

Output: `apps/<app>/build/outputs/void/void-<app>-X.Y.Z.apk`, already verified with
`apksigner verify --print-certs`. Check that the printed certificate DN is the app's key,
not a debug key.

## 6. Publish (each step needs the owner's explicit OK)

```bash
git tag <app>/vX.Y.Z
git push origin main <app>/vX.Y.Z
gh release create <app>/vX.Y.Z apps/<app>/build/outputs/void/void-<app>-X.Y.Z.apk \
  --title "Void <Display Name> X.Y.Z" --notes "<release notes>"
```

Release notes: user-facing changes, one bullet each, plus "Requires Android <minSdk
version> or later."

## 7. Install (optional)

```bash
adb install -r apps/<app>/build/outputs/void/void-<app>-X.Y.Z.apk
```

`-r` keeps data. An "INSTALL_FAILED_UPDATE_INCOMPATIBLE" error means the installed copy
was signed with a different key (e.g. a debug build): uninstall it first only with the
owner's consent, since that deletes the app's data.
