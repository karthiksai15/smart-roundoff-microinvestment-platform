#!/bin/bash

ROOT_DIR=$(pwd)

echo "======================================"
echo " STARTING SMART ROUND-OFF PLATFORM "
echo "======================================"

echo "🚀 Starting Docker Desktop..."
open -a Docker

echo "⏳ Waiting for Docker..."
while ! docker info > /dev/null 2>&1; do
  sleep 2
done

echo "✅ Docker is running"

echo "🐳 Starting containers (Postgres, Redis, Kafka)..."
docker compose up -d

echo "⏳ Waiting for infrastructure..."
sleep 10

echo "🧭 Starting Eureka Service Registry (8761)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :service-registry:bootRun\""
sleep 12

echo "🔐 Starting Auth Service (8081)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :auth-service:bootRun\""
sleep 6

echo "💳 Starting Payment Service (8082)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :payment-service:bootRun\""
sleep 4

echo "📊 Starting Investment Service (8083)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :investment-service:bootRun\""
sleep 4

echo "🚨 Starting Fraud Service (8084)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :fraud-service:bootRun\""
sleep 4

echo "🔔 Starting Notification Service (8085)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :notification-service:bootRun\""
sleep 4

echo "🌐 Starting API Gateway (9000)..."
osascript -e "tell app \"Terminal\" to do script \"cd $ROOT_DIR && ./gradlew :api-gateway:bootRun\""

echo "======================================"
echo " ALL SERVICES STARTED 🚀 "
echo "======================================"
