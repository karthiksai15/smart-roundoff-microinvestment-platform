#!/bin/bash

echo "======================================"
echo " STOPPING SROMIP PLATFORM "
echo "======================================"

PORTS=(8761 9000 8081 8083 8084 8085 8088 8089 8092 8095 8000)

echo "🛑 Stopping services..."

for PORT in "${PORTS[@]}"
do
  PID=$(lsof -ti:$PORT)

  if [ -n "$PID" ]; then
    echo "➡️ Stopping port $PORT"
    kill -15 $PID
    sleep 3

    if kill -0 $PID 2>/dev/null; then
      echo "⚠️ Force killing $PORT"
      kill -9 $PID
    fi
  fi
done

echo "🐳 Stopping Docker..."
docker compose down --remove-orphans

echo "✅ Platform stopped"