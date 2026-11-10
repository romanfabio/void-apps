---
name: add-dependency
description: Add or bump a library or Gradle plugin in gradle/libs.versions.toml with license verification and owner approval. Use whenever code needs a dependency not already in the catalog, or a version change is requested.
---

# add-dependency

`gradle/libs.versions.toml` is a protected file (CLAUDE.md §6): **no change without the
owner's explicit approval in this conversation.**

## 1. Is it needed?

- Check the catalog first; reuse an existing entry if one fits.
- Forbidden regardless of license: analytics, crash reporting, ads, telemetry, Firebase,
  Koin, Retrofit/OkHttp (no network by default), RxJava, LiveData, Material Icons Extended,
  mocking frameworks, kapt-only processors, anything from JitPack or `mavenLocal()`.
- Prefer AndroidX / Kotlin first-party libraries over third-party ones.

## 2. Check the license (runtime dependencies)

1. Open the artifact's POM on Maven Central or Google Maven and read `<licenses>`.
2. Allowed SPDX ids: `Apache-2.0`, `MIT`, `BSD-2-Clause`, `BSD-3-Clause`, `ISC`.
   Anything else → stop and report. Test-only dependencies with other licenses need an
   explicit owner decision (docs/TESTING.md §1).
3. Check transitive dependencies after adding (step 5); they must pass too.

## 3. Pick the version

- Latest **stable** release only — no `alpha`, `beta`, `rc`, `dev`, `SNAPSHOT`.
- Must be compatible with the current AGP, Kotlin and compileSdk.

## 4. Ask for approval

Present to the owner and wait for a clear yes:

> Add `<group:artifact>` `<version>` (license `<SPDX>`), used by `<module>` for `<reason>`.
> Alternatives considered: `<existing catalog entry or none>`.

## 5. Apply

1. Catalog entry, alphabetical within its section:
   - version key: lowerCamelCase (`room`, `kotlinxSerialization`)
   - library alias: kebab-case from the artifact (`androidx-room-runtime`)
   - plugin alias: kebab-case (`kotlin-serialization`)
2. Reference only via `libs.*` in the module's `build.gradle.kts` (never a version literal).
   Shared configuration belongs in a convention plugin (`build-logic/`, also protected).
3. Run:

   ```bash
   ./gradlew :apps:<app>:prepareLibraryDefinitionsRelease :apps:<app>:check
   ```

   `prepareLibraryDefinitionsRelease` fails if any runtime dependency (including
   transitives) is outside the allowlist.
4. If an app uses it directly, add a row to "App-specific dependencies" in its README.

## 6. Commit

`build(<scope>): add <library> <version>` (or `bump … to …`), body line
`Approved-by: owner`.
