# Security Policy

## Supported Versions
This project is actively maintained in the `main` branch.

## Reporting a Vulnerability
If you discover a security issue:
1. Do not post exploit details publicly.
2. Open a private security report through GitHub Security Advisories or contact the maintainer directly.
3. Include reproduction details, affected files, and impact.

## Repository Security Decisions
- `assets/data.bin` is excluded from the public repository.
- Release signing files (`.jks`, `.keystore`, `.pem`, `.key`, `.p12`) are excluded by `.gitignore`.
- Backup and data extraction are disabled by default in app manifest config.
- Cleartext network traffic is disabled using network security config.

## Hardening Checklist
- Keep payload artifacts in private storage only.
- Use Play App Signing or securely managed private keystores.
- Rotate signing credentials if compromise is suspected.
- Never publish secrets in commits, issues, or release notes.
