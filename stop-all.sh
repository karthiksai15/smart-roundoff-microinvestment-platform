#!/bin/bash

echo "======================================"
echo " STOPPING SROMIP PLATFORM "
echo " CLEAN SHUTDOWN MODE "
echo "======================================"

# -------------------------------
# Define service ports
# -------------------------------
PORTS=(8761 9000 8081 8083 8084 8085 8088 8089 8092 8095)

# -------------------------------
# Stop Spring Boot Services
# -------------------------------
echo "🛑 Stopping Spring Boot microservices..."

for PORT in "${PORTS[@]}"
do
  PID=$(lsof -ti:$PORT)

  if [ ! -z "$PID" ]; then
    echo "Stopping service on port $PORT (PID: $PID)"

    # Try graceful shutdown first
    kill -15 $PID 2>/dev/null
    sleep 2

    # Force kill if still running
    if kill -0 $PID 2>/dev/null; then
      echo "Force killing service on port $PORT"
      kill -9 $PID 2>/dev/null
    fi
  else
    echo "No service running on port $PORT"
  fi
done

echo "✅ Spring Boot services stopped"

# -------------------------------
# Stop ML Fraud Service
# -------------------------------
echo "🧠 Stopping ML Fraud service..."

ML_PID=$(lsof -ti:8000)

if [ ! -z "$ML_PID" ]; then
  echo "Stopping ML service (PID: $ML_PID)"
  kill -15 $ML_PID
  sleep 2

  if kill -0 $ML_PID 2>/dev/null; then
    kill -9 $ML_PID
  fi

  echo "✅ ML service stopped"
else
  echo "ML service not running"
fi

# -------------------------------
# Stop Docker Infrastructure
# -------------------------------
echo "🐳 Stopping Docker infrastructure..."

docker compose down --remove-orphans

echo "✅ Docker containers stopped"

# -------------------------------
# Cleanup leftover ports
# -------------------------------
echo "🔎 Verifying ports are free..."

for PORT in "${PORTS[@]}"
do
  PID=$(lsof -ti:$PORT)

  if [ ! -z "$PID" ]; then
    echo "Cleaning leftover process on port $PORT"
    kill -9 $PID 2>/dev/null
  fi
done

# Check ML port again
ML_PID=$(lsof -ti:8000)

if [ ! -z "$ML_PID" ]; then
  kill -9 $ML_PID 2>/dev/null
fi

echo "======================================"
echo " PLATFORM STOPPED SUCCESSFULLY 🛑 "
echo "======================================"

echo ""
echo "All services stopped:"
echo "✔ API Gateway"
echo "✔ Auth Service"
echo "✔ Payment Intent Service"
echo "✔ Fraud Service"
echo "✔ Payment Service"
echo "✔ OTP Service"
echo "✔ Investment Service"
echo "✔ Notification Service"
echo "✔ Dashboard Service"
echo "✔ ML Fraud Detection"
echo "✔ Kafka"
echo "✔ Redis"
echo "✔ PostgreSQL"
echo "✔ ELK Stack"