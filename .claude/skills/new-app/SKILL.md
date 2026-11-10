---
name: new-app
description: Create a new Void Android app in apps/<app> from apps/_template, following CLAUDE.md and docs/. Use when the owner asks to start, scaffold, or add a new app to the monorepo.
---

# new-app

Creates `apps/<app>/` from `apps/_template/`, renames package and classes, registers the
module, and produces a compliant README. Registering a new app in `settings.gradle.kts`
does not need approval; everything else listed in `CLAUDE.md` §6 still does.

## 1. Collect inputs (ask the owner for anything missing — never invent)

| Input | Rule | Example |
|---|---|---|
| `<app>` | `^[a-z][a-z0-9]*$`, ≤ 20 chars, no `void`, not a Java/Kotlin keyword | `scanner` |
| Display name | Shown as `Void <Display Name>` | `Scanner` |
| One-sentence purpose | User-facing | "Scans documents to PDF." |
| minSdk | Default 28; any other value needs a reason (docs/ANDROID_SUPPORT.md §3) | `28` |
| Permissions needed | Each one needs owner approval | none |

## 2. Generate

Run from the repository root (Git Bash on Windows):

```bash
bash .claude/skills/new-app/scripts/create-app.sh <app> "<Display Name>"
```

The script copies the template (without `build/` and `schemas/`), moves packages to
`io.github.romanfabio.voidapps.<app>`, renames `Template*` classes to `<Name>*`, sets the
app name, resets `version.properties` to `0.1.0`, and appends
`include(":apps:<app>")` to `settings.gradle.kts`.

## 3. Finish by hand

1. **settings.gradle.kts:** keep the `include(":apps:…")` lines alphabetical below the
   `// Apps` comment.
2. **minSdk:** if not 28, set `defaultConfig { minSdk = N }` in `apps/<app>/build.gradle.kts`.
3. **README:** rewrite `apps/<app>/README.md` from `docs/README_TEMPLATE.md` — every
   section present, every placeholder filled, `None.` where empty.
4. **Root README:** add a row to the app table in `/README.md`.
5. **Sample feature:** `feature/home` (items list) is sample code. Keep it until the first
   real screen is added with the `add-screen` skill; that change deletes the sample
   (`Item*`, `ItemDao`, `HomeDataModule`, `Fake*`, their tests, and the entity in
   `<Name>Database`) and resets the database to version 1.
6. **Launcher icon:** the placeholder ring stays until the owner provides artwork; record
   it under "Open decisions" in the README.
7. **Keystore:** tell the owner to create `keystores/<app>.jks` and add its passwords to
   `keystores/keystore.properties` (command in `docs/SECURITY.md` §1). Never create real
   signing keys yourself.

## 4. Verify and commit

1. Run the `verify` skill for `:apps:<app>`. It must pass.
2. Commit on branch `feat/<app>/create-app`:
   `feat(<app>): create app from template`.
