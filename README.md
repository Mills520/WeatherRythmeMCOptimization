# WeatherRythme MC Optimization

This repository contains brand-new dual-loader Minecraft mod sources for **Fabric** and **Forge**.

## What the mod does
- Randomly changes overworld weather every **5 to 15 minutes**.
- Runs on the **server tick** so all players see synchronized weather.
- Uses the required attribution text only:
  - Made by Mills520
  - Thanks to all contributors on GitHub under the contributors tag

## Target versions
The requested range `1.17-1.21.11` is interpreted as Java Edition releases from **1.17.1 through 1.21.1**.

## Build artifacts locally
Use the helper script to build Fabric and Forge jars for each targeted version:

```bash
./scripts/build_all.sh
```

Build output is copied into:

- `dist/fabric/<mc_version>/`
- `dist/forge/<mc_version>/`

Each build is configured to produce:
- main mod jar
- sources jar

Note: binary icon assets were intentionally removed to keep patch/PR flows compatible with platforms that reject binary diffs.


## Build artifacts on GitHub Actions
A workflow is included at `.github/workflows/build-jars.yml` to compile all version/loader combinations and upload artifact bundles automatically on pushes, pull requests, or manual dispatch.
