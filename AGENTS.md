# agent-repair

## Cursor Cloud specific instructions

- The Node.js toolchain is pre-installed via `nvm` (default `v22.22.2`); `npm`, `pnpm`, and `yarn` are all available on `PATH`.
- This repository currently contains no application code or dependency manifest (no `package.json`/lockfile), so there is nothing to build or run yet. Once an app is added, install dependencies with the package manager matching the committed lockfile (`package-lock.json` → npm, `pnpm-lock.yaml` → pnpm, `yarn.lock` → yarn) and run the dev server via that project's scripts.
