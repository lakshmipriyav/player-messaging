#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR="$PROJECT_DIR/target/player-messaging.jar"
PORT="${1:-9090}"
RESPONDER_LOG="/tmp/player2_responder.log"

echo ">>> Building..."
cd "$PROJECT_DIR" && mvn -q package -DskipTests

echo ""
echo ">>> Starting RESPONDER (Player2) on port $PORT..."
java -cp "$JAR" com.360t.players.multiprocess.ResponderMain "$PORT" \
    > "$RESPONDER_LOG" 2>&1 &
RESPONDER_PID=$!
echo "    PID : $RESPONDER_PID"
echo "    Log : $RESPONDER_LOG"

sleep 1

echo ""
echo ">>> Starting INITIATOR (Player1) — connecting to localhost:$PORT..."
java -cp "$JAR" com.360t.players.multiprocess.InitiatorMain localhost "$PORT"

echo ""
echo ">>> Waiting for responder to finish..."
wait "$RESPONDER_PID" || true

echo ""
echo ">>> Responder output:"
echo "────────────────────────────────────────"
cat "$RESPONDER_LOG"
echo "────────────────────────────────────────"
echo ""
echo ">>> Multi-process run complete."