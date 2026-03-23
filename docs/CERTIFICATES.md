# Certificates & Signing Guidance

This repository does **not** include private signing certificates by design.

## Why certificates are not committed
Signing keys are highly sensitive. If exposed, attackers can publish malicious updates signed as your app.

## Recommended release model
- Use **Google Play App Signing** for Play distribution.
- Keep the upload key private (outside this repository).
- Store signing credentials in a secure password manager or secret vault.

## Files that must never be committed
- `*.jks`
- `*.keystore`
- `*.pem`
- `*.key`
- `*.p12`

These patterns are already ignored in `.gitignore`.

## Optional verification artifacts you may publish
If needed, you can publish only non-sensitive fingerprints in release notes:
- SHA-256 certificate fingerprint
- APK checksum (SHA-256)

Do not publish private key material.
