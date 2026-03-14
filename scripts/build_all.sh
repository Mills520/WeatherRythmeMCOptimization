#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DIST_DIR="$ROOT_DIR/dist"
mkdir -p "$DIST_DIR"

# Minecraft versions requested by user (1.21.11 interpreted as latest Java release line 1.21.1).
VERSIONS=("1.17.1" "1.18.2" "1.19.2" "1.20.1" "1.21.1")

# Fabric dependency versions aligned to each Minecraft target.
declare -A FABRIC_API=(
  ["1.17.1"]="0.46.1+1.17"
  ["1.18.2"]="0.77.0+1.18.2"
  ["1.19.2"]="0.77.0+1.19.2"
  ["1.20.1"]="0.92.2+1.20.1"
  ["1.21.1"]="0.115.1+1.21.1"
)

declare -A LOADER=(
  ["1.17.1"]="0.14.25"
  ["1.18.2"]="0.15.11"
  ["1.19.2"]="0.15.11"
  ["1.20.1"]="0.16.10"
  ["1.21.1"]="0.16.10"
)

declare -A FORGE=(
  ["1.17.1"]="37.1.1"
  ["1.18.2"]="40.2.21"
  ["1.19.2"]="43.4.2"
  ["1.20.1"]="47.3.0"
  ["1.21.1"]="52.0.30"
)

for v in "${VERSIONS[@]}"; do
  echo "==> Building Fabric $v"
  (cd "$ROOT_DIR" && gradle -p fabric clean build \
    -Pminecraft_version="$v" \
    -Pyarn_mappings="${v}+build.1" \
    -Pfabric_loader_version="${LOADER[$v]}" \
    -Pfabric_api_version="${FABRIC_API[$v]}")

  mkdir -p "$DIST_DIR/fabric/$v"
  cp "$ROOT_DIR/fabric/build/libs"/*.jar "$DIST_DIR/fabric/$v/"

  echo "==> Building Forge $v"
  (cd "$ROOT_DIR" && gradle -p forge clean build \
    -Pminecraft_version="$v" \
    -Pforge_version="${FORGE[$v]}")

  mkdir -p "$DIST_DIR/forge/$v"
  cp "$ROOT_DIR/forge/build/libs"/*.jar "$DIST_DIR/forge/$v/"
done

echo "Built artifacts are in $DIST_DIR"
