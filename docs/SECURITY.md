# Security

## 1. Signing keys

### Layout
```
keystores/
├── README.md              ← committed (explains this folder)
├── keystore.properties    ← NEVER committed
├── scanner.jks            ← NEVER committed
└── <app>.jks              ← NEVER committed
```

- **One keystore per app**: `keystores/<app>.jks`, format PKCS12, containing one key with
  alias `<app>`. Why: a leaked key compromises only one app, and an app can later be
  handed over without exposing the others.
- `keystores/keystore.properties` holds the passwords for all apps:

  ```properties
  scanner.storePassword=…
  scanner.keyPassword=…
  ```

- Key generation (run from the repository root; use a password of ≥ 20 random characters
  from a password manager):

  ```bash
  keytool -genkeypair -v -storetype PKCS12 \
    -keystore keystores/<app>.jks -alias <app> \
    -keyalg RSA -keysize 4096 -validity 10000 \
    -dname "CN=Void <Display Name>, O=romanfabio"
  ```

- The `void.android.application` plugin reads `keystores/keystore.properties` and
  `keystores/<app>.jks`. If either is missing, the `release` build type is **unsigned**
  (CI) and `exportReleaseApk` **fails** (local release). It never falls back to the debug
  key.
- Debug builds use the standard Android debug key (`~/.android/debug.keystore`), which is
  never used for distributed APKs.

### Backup and loss
- Losing `<app>.jks` or its password means installed copies can never be updated again;
  users would have to uninstall (losing data) and install a newly signed app.
- ⚠️ DECISION NEEDED: off-repository backup location for `keystores/` (e.g. an encrypted
  password-manager attachment). Until decided, the release skill warns before every
  release that no backup location is documented.
- Passwords are stored in a password manager in addition to `keystore.properties`.

### Compromise
If a key or its password is exposed: stop distributing that app, generate a new key,
change nothing else, publish a new release, and record in the app README that
existing installs must be reinstalled.

## 2. Never commit

Enforced by `.gitignore` **and** by CI (job `changes` fails if any of these paths is
tracked):

| Pattern | Why |
|---|---|
| `*.jks`, `*.keystore`, `*.p12`, `*.pk8`, `*.pem`, `*.key` | Signing material |
| `keystores/keystore.properties`, `**/signing.properties` | Signing passwords |
| `local.properties` | Machine-specific SDK path |
| `.env`, `.env.*`, `*.secrets.properties` | Secrets |
| `google-services.json`, `*-service-account*.json` | Third-party credentials (not used) |
| `*.apk`, `*.aab`, `build/`, `.gradle/`, `.kotlin/` | Build outputs |
| `.idea/` (except shared code style), `*.iml`, `captures/` | IDE state |

If a secret is ever committed: treat it as compromised (rotate it, §1 "Compromise"), then
remove it from history. Deleting it in a later commit is not enough.

## 3. Secrets and environment variables

- There are **no** runtime secrets: apps have no backend and no API keys.
- There are **no** CI secrets: CI does not sign or publish.
- If a future feature needs a secret (requires approval): it is read from
  `local.properties`-style ignored files or environment variables at build time, never
  hard-coded, never stored in `BuildConfig` of a distributed APK unless it is safe to be
  public.

## 4. App hardening (every app)

1. `android:allowBackup="false"`, plus `android:dataExtractionRules="@xml/data_extraction_rules"`
   (Android 12+) and `android:fullBackupContent="@xml/backup_rules"` (Android 11 and lower),
   both excluding all domains.
   Why: data never leaves the device without the user's explicit action.
2. No `android.permission.INTERNET` unless approved (`CLAUDE.md` §6).
3. `android:usesCleartextTraffic="false"`.
4. Only `MainActivity` is `android:exported="true"`; every other component is
   `exported="false"` unless approved.
5. Release builds: `isMinifyEnabled = true`, `isShrinkResources = true`,
   `isDebuggable = false` (configured by the convention plugin; not overridable in apps).
6. No logging of user data. Release builds strip `Log.v`, `Log.d`, `Log.i` via R8
   `-assumenosideeffects` in the shared ProGuard rules.
7. `PendingIntent`s use `FLAG_IMMUTABLE` unless mutability is required and documented.
8. Files are written to app-specific storage (`filesDir`, `cacheDir`); shared storage only
   through the Storage Access Framework or `MediaStore`.
9. `WebView` is forbidden unless approved.

## 5. Supply chain

- Repositories: `google()` and `mavenCentral()` for libraries; `gradlePluginPortal()`
  only for Gradle plugins. No other repositories, no `mavenLocal()`, no JitPack.
- Every dependency must pass the license allowlist (`CLAUDE.md` §5) and be added via the
  `add-dependency` skill.
- The Gradle wrapper pins `distributionSha256Sum`; CI validates the wrapper jar
  (`gradle/actions/setup-gradle` wrapper validation).
- GitHub Actions: only the actions listed in `CICD.md` §2, with
  `permissions: contents: read`.

## 6. Reporting

⚠️ DECISION NEEDED: enable GitHub private vulnerability reporting and secret-scanning push
protection on the repository once it exists (GitHub settings, not files in this repo).
