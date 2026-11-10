# keystores/

Local release-signing material. **Everything in this folder except this README is
git-ignored and must never be committed** (docs/SECURITY.md §2).

Expected contents on the release machine:

```
keystores/
├── keystore.properties   # <app>.storePassword=… / <app>.keyPassword=…
└── <app>.jks             # one PKCS12 keystore per app, key alias = <app>
```

- Create a keystore: see docs/SECURITY.md §1 ("Key generation").
- Back up every `.jks` and its password outside this repository.
  ⚠️ DECISION NEEDED: backup location (docs/SECURITY.md §1).
- Losing a keystore means installed copies of that app can never be updated.
