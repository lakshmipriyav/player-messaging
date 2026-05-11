#!/usr/bin/env bash
# Builds the project and runs both players in the SAME JVM.
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR="$PROJECT_DIR/target/player-messaging.jar"

echo ">>> Building..."
cd "$PROJECT_DIR" && mvn -q package -DskipTests

echo -e "\n>>> Running IN-PROCESS mode (same JVM, two threads)\n"
java -jar "$JAR"